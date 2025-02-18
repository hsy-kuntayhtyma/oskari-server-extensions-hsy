package flyway.seutumassa2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.oskari.helpers.AppSetupHelper;
import org.oskari.helpers.BundleHelper;

public class V1_00_0__add_lang_override extends BaseJavaMigration {

    private static final String LANG_OVERRIDES = "hsy-lang-overrides";

    public void migrate(Context context) throws Exception {
        Connection c = context.getConnection();
        BundleHelper.registerBundle(c, LANG_OVERRIDES);
        for (long appsetupId : AppSetupHelper.getSetupsForType(c)) {
            if (AppSetupHelper.appContainsBundle(c, appsetupId, LANG_OVERRIDES)) {
                continue;
            }
            incrementSeqNos(c, appsetupId);
            addBundleToApp(c, appsetupId, LANG_OVERRIDES, 0);
        }
    }

    private static int incrementSeqNos(Connection c, long appsetupId) throws SQLException {
        String sql = "UPDATE oskari_appsetup_bundles SET seqno = seqno + 1 WHERE appsetup_id = ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, appsetupId);
            return ps.executeUpdate();
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
