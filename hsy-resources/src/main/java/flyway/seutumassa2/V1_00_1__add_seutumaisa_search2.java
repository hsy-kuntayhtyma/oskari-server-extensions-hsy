package flyway.seutumassa2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.oskari.helpers.AppSetupHelper;
import org.oskari.helpers.BundleHelper;

import fi.nls.oskari.domain.map.view.Bundle;
import fi.nls.oskari.log.LogFactory;
import fi.nls.oskari.log.Logger;

public class V1_00_1__add_seutumaisa_search2 extends BaseJavaMigration {

    private static final Logger LOG = LogFactory.getLogger(V1_00_1__add_seutumaisa_search2.class);

    private static final String SEUTUMAISA_SEARCH = "seutumaisa-search";
    private static final String SEARCH = "search";

    public void migrate(Context context) throws Exception {
        Connection c = context.getConnection();

        for (long appsetupId : getSeutumaisaAppsetupIds(c)) {
            if (!AppSetupHelper.appContainsBundle(c, appsetupId, SEUTUMAISA_SEARCH)) {
                addBundleToAppsetupBeforeBundle(c, appsetupId, SEUTUMAISA_SEARCH, SEARCH);
            }
        }
    }

    static List<Long> getSeutumaisaAppsetupIds(Connection c) throws SQLException {
        List<Long> ids = new ArrayList<>();

        String sql = "select id from oskari_appsetup where page = 'index_SeutuMaisa'";
        try (PreparedStatement ps = c.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ids.add(rs.getLong("id"));
            }
        }

        return ids;
    }

    private static void addBundleToAppsetupBeforeBundle(Connection c, long appsetupId, String bundleA, String bundleB) throws SQLException {
        BundleHelper.registerBundle(c, bundleA);

        Bundle b = BundleHelper.getRegisteredBundle(c, bundleB);
        if (b == null) {
            LOG.warn("Bundle not registered!", bundleB);
            return;
        }

        int seqNo = getSeqNo(c, appsetupId, b.getBundleId(), -1);
        if (seqNo == -1) {
            LOG.debug("Bundle", bundleB, "not part of appsetup", appsetupId, "skipping...");
            return;
        }
        
        incrementSeqNos(c, appsetupId, seqNo);
        addBundleToApp(c, appsetupId, bundleA, seqNo);
    }

    private static int getSeqNo(Connection c, long appsetupId, long bundleId, int fallback) throws SQLException {
        String sql = "SELECT seqno FROM oskari_appsetup_bundles WHERE appsetup_id = ? AND bundle_id = ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, appsetupId);
            ps.setLong(2, bundleId);
            try (ResultSet rs = ps.getResultSet()) {
                return rs.next() ? rs.getInt(1) : fallback;
            }
        }
    }
    
    private static void incrementSeqNos(Connection c, long appsetupId, int fromSeqNo) throws SQLException {
        int max = getMaxSeqNo(c, appsetupId, -1);
        if (max == -1) {
            return;
        }

        String incr = "UPDATE oskari_appsetup_bundles SET seqno = seqno + " + (max + 1) + " WHERE appsetup_id = ? AND seqno >= ?";
        try (PreparedStatement ps = c.prepareStatement(incr)) {
            ps.setLong(1, appsetupId);
            ps.setInt(2, fromSeqNo);
            ps.executeUpdate();
        }
        
        String decr = "UPDATE oskari_appsetup_bundles SET seqno = seqno - " + max + " WHERE appsetup_id = ? AND seqno >= ?";
        try (PreparedStatement ps = c.prepareStatement(decr)) {
            ps.setLong(1, appsetupId);
            ps.setInt(2, fromSeqNo); // could be fromSeqNo+max, but no need
            ps.executeUpdate();
        }
    }
    
    private static int getMaxSeqNo(Connection c, long appsetupId, int fallback) throws SQLException {
        String q = "SELECT MAX(seqno) FROM oskari_appsetup_bundles WHERE appsetup_id = ?";
        try (PreparedStatement ps = c.prepareStatement(q)) {
            ps.setLong(1, appsetupId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : fallback;
            }
        }
    }

    private static void addBundleToApp(Connection c, long appsetupId, String bundleName, long seqNo)
            throws SQLException {
        final String sql = "INSERT INTO oskari_appsetup_bundles " +
                "(appsetup_id, bundle_id, seqno, config, state, bundleinstance) " +
                "VALUES (" +
                "?, " +
                "(SELECT id FROM oskari_bundle WHERE name=?), " +
                "?, " +
                "(SELECT config FROM oskari_bundle WHERE name=?), " +
                "(SELECT state FROM oskari_bundle WHERE name=?),  " +
                "?)";
        try (final PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, appsetupId);
            ps.setString(2, bundleName);
            ps.setLong(3, seqNo);
            ps.setString(4, bundleName);
            ps.setString(5, bundleName);
            ps.setString(6, bundleName);
            ps.execute();
        }
    }

}
