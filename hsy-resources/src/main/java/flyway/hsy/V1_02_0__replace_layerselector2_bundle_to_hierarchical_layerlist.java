package flyway.hsy;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import fi.nls.oskari.domain.map.view.Bundle;
import fi.nls.oskari.domain.map.view.View;
import fi.nls.oskari.log.LogFactory;
import fi.nls.oskari.log.Logger;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.oskari.helpers.BundleHelper;

public class V1_02_0__replace_layerselector2_bundle_to_hierarchical_layerlist extends BaseJavaMigration {
    private static final Logger LOG = LogFactory.getLogger(V1_02_0__replace_layerselector2_bundle_to_hierarchical_layerlist.class);

    private static final String BUNDLE_LAYERSELECTOR2 = "layerselector2";
    private static final String BUNDLE_HIERARCHICAL_LAYERLIST = "hierarchical-layerlist";

    private int updatedViewCount = 0;

    public void migrate(Context context) throws Exception {
        try {
            updateViews(context.getConnection());
        }
        finally {
            LOG.info("Updated views:", updatedViewCount);
        }
    }

    private void updateViews(Connection conn)
            throws Exception {
        List<View> list = getOutdatedViews(conn);
        LOG.info("Got", list.size(), "outdated views");
        for(View view : list) {
            addHierarchicalLayerListBundle(conn, view.getId());
            updatedViewCount++;
        }
    }

    private List<View> getOutdatedViews(Connection conn) throws SQLException {

        List<View> list = new ArrayList<>();
        final String sql = "SELECT id FROM oskari_appsetup " +
                "WHERE (type = 'USER' OR type = 'DEFAULT') AND " +
                "id IN (" +
                "SELECT distinct appsetup_id FROM oskari_appsetup_bundles WHERE bundle_id IN (" +
                "SELECT id FROM oskari_bundle WHERE name='layerselection2' OR name='layerselector2'" +
                "));";
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    View view = new View();
                    view.setId(rs.getLong("id"));
                    list.add(view);
                }
            }
        }
        return list;
    }


    public void addHierarchicalLayerListBundle(Connection conn, final long viewId) throws SQLException {
        Bundle layerselectorBundle = BundleHelper.getRegisteredBundle(conn, BUNDLE_LAYERSELECTOR2);
        if( layerselectorBundle == null) {
            // not even registered so migration not needed
            return;
        }
        Bundle newBundle = BundleHelper.getRegisteredBundle(conn, BUNDLE_HIERARCHICAL_LAYERLIST);
        if(newBundle == null) {
            throw new RuntimeException("Bundle not registered: " + BUNDLE_HIERARCHICAL_LAYERLIST);
        }

        // update layerselector2 bundle to hierarchical-layerlist
        replaceLayerselectorBundleToHierarchicalLayerlist(conn, viewId, layerselectorBundle, newBundle);

    }

    public void replaceLayerselectorBundleToHierarchicalLayerlist(Connection conn, final long viewId, final Bundle oldBundle, final Bundle newBundle) throws SQLException {
        final String sql = "UPDATE oskari_appsetup_bundles " +
                "SET " +
                "    bundle_id=?, " +
                "    startup=?, " +
                "    bundleinstance=?" +
                "WHERE bundle_id = ? and appsetup_id=?";

        try (PreparedStatement statement =
                     conn.prepareStatement(sql)){
            statement.setLong(1, newBundle.getBundleId());
            statement.setString(2, newBundle.getStartup());
            statement.setString(3, newBundle.getName());
            statement.setLong(4, oldBundle.getBundleId());
            statement.setLong(5, viewId);
            statement.execute();
        }
    }
}
