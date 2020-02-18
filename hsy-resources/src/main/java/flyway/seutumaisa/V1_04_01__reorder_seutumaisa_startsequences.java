package flyway.seutumaisa;

import fi.nls.oskari.domain.map.view.Bundle;
import fi.nls.oskari.domain.map.view.View;
import fi.nls.oskari.map.view.ViewService;
import fi.nls.oskari.map.view.ViewServiceIbatisImpl;
import org.flywaydb.core.api.migration.jdbc.JdbcMigration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class V1_04_01__reorder_seutumaisa_startsequences implements JdbcMigration {
    private static final ViewService VIEW_SERVICE = new ViewServiceIbatisImpl();
    private static final String ROLE_SEUTUMAISA = "seutumaisa";
    private static final String BUNDLE_DIVMANAZER = "divmanazer";
    private static final String BUNDLE_SEUTUMAISA_HISTORY_SEARCH = "seutumaisa-history-search";
    private static final String BUNDLE_SEUTUMAISA_SEARCH = "seutumaisa-search";

    public void migrate(Connection connection) throws SQLException {
        long viewId = VIEW_SERVICE.getDefaultViewIdForRole(ROLE_SEUTUMAISA);

        List<Bundle> viewBundles = getViewBundles(connection, viewId);
        reorderBundles(connection, viewBundles, viewId);
    }

    private List<Bundle> getViewBundles(Connection conn, long viewId) throws SQLException {
        List<Bundle> list = new ArrayList<>();
        final String sql = "SELECT bundle_id, seqno, bundleinstance FROM portti_view_bundle_seq " +
                "WHERE view_id = ? ORDER BY seqno ASC;";

        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setLong(1, viewId);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    Bundle bundle = new Bundle();
                    bundle.setViewId(viewId);
                    bundle.setSeqNo(rs.getInt("seqno"));
                    bundle.setBundleId(rs.getLong("bundle_id"));
                    bundle.setBundleinstance(rs.getString("bundleinstance"));

                    list.add(bundle);
                }
            }
        }

        return list;
    }

    private void reorderBundles(Connection conn, List<Bundle> bundles, long viewId) throws SQLException {
        int index = 0;
        for(int i=0; i<bundles.size(); i++) {
            Bundle bundle = bundles.get(i);
            if (!BUNDLE_DIVMANAZER.equals(bundle.getBundleinstance())) {
                updateBundleSeqNo(conn, bundle.getBundleId(), index, viewId);
            } else {
                updateBundleSeqNo(conn, bundle.getBundleId(), index, viewId);
                index++;
                updateSeutumaisaHistorySearchBundleSeqNo(conn, bundles, index, viewId);
                index++;
                updateSeutumaisaSearchBundleSeqNo(conn, bundles, index, viewId);
            }
            index++;

        }

    }

    private void updateSeutumaisaHistorySearchBundleSeqNo(Connection conn, List<Bundle> bundles, int newSeqNo, long viewId) throws SQLException {
        for(int i=0; i<bundles.size(); i++) {
            Bundle bundle = bundles.get(i);
            if (BUNDLE_SEUTUMAISA_HISTORY_SEARCH.equals(bundle.getBundleinstance())) {
                updateBundleSeqNo(conn, bundle.getBundleId(), newSeqNo, viewId);
            }
        }
    }



    private void updateBundleSeqNo(Connection conn, long bundleId, int newSeqNo, long viewId) throws SQLException {
        final String sql = "UPDATE portti_view_bundle_seq " +
                "SET seqno=? " +
                "WHERE bundle_id = ? and view_id=?";

        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, newSeqNo);
            statement.setLong(2, bundleId);
            statement.setLong(3, viewId);
            statement.execute();
        }
    }

    private void updateSeutumaisaSearchBundleSeqNo(Connection conn, List<Bundle> bundles, int newSeqNo, long viewId) throws SQLException {
        for(int i=0; i<bundles.size(); i++) {
            Bundle bundle = bundles.get(i);
            if (BUNDLE_SEUTUMAISA_SEARCH.equals(bundle.getBundleinstance())) {
                updateBundleSeqNo(conn, bundle.getBundleId(), newSeqNo, viewId);
            }
        }
    }

}
