package flyway.hsy;

import fi.nls.oskari.domain.map.view.Bundle;
import fi.nls.oskari.domain.map.view.View;
import fi.nls.oskari.log.LogFactory;
import fi.nls.oskari.log.Logger;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.oskari.helpers.BundleHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class V1_02_1__remove_layerselection_bundle_from_default_views extends BaseJavaMigration {
    private static final Logger LOG = LogFactory.getLogger(V1_02_1__remove_layerselection_bundle_from_default_views.class);

    private static final String BUNDLE_LAYERSELECTION2 = "layerselection2";

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
            removeLayerselectionBundle(conn, view.getId());
            updatedViewCount++;
        }
    }

    private List<View> getOutdatedViews(Connection conn) throws SQLException {

        List<View> list = new ArrayList<>();
        final String sql = "SELECT id FROM oskari_appsetup " +
                "WHERE (type = 'USER' OR type = 'DEFAULT') AND " +
                "id IN (" +
                "SELECT distinct appsetup_id FROM oskari_appsetup_bundles WHERE bundle_id IN (" +
                "SELECT id FROM oskari_bundle WHERE name='layerselection2'" +
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


    public void removeLayerselectionBundle(Connection conn, final long viewId) throws SQLException {
        Bundle layerselectionBundle = BundleHelper.getRegisteredBundle(conn, BUNDLE_LAYERSELECTION2);
        if(layerselectionBundle == null) {
            // not even registered so migration not needed
            return;
        }

        // remove layerselection2 bundle
        final String sql = "DELETE FROM oskari_appsetup_bundles " +
                "WHERE bundle_id = ? AND appsetup_id=?;";

        try (PreparedStatement statement =
                     conn.prepareStatement(sql)){
            statement.setLong(1, layerselectionBundle.getBundleId());
            statement.setLong(2, viewId);
            statement.executeUpdate();
        }

    }
}
