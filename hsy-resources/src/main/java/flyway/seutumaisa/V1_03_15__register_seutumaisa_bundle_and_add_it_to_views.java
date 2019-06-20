package flyway.seutumaisa;

import fi.nls.oskari.db.BundleHelper;
import fi.nls.oskari.domain.map.view.Bundle;
import fi.nls.oskari.map.view.ViewService;
import fi.nls.oskari.map.view.ViewServiceIbatisImpl;
import fi.nls.oskari.util.FlywayHelper;
import org.flywaydb.core.api.migration.jdbc.JdbcMigration;

import java.sql.Connection;
import java.sql.SQLException;

public class V1_03_15__register_seutumaisa_bundle_and_add_it_to_views implements JdbcMigration {

    private static final String BUNDLE = "seutumaisa-search";
    private static final ViewService VIEW_SERVICE = new ViewServiceIbatisImpl();
    private static final String ROLE_SEUTUMAISA = "seutumaisa";

    public void migrate(Connection connection) throws SQLException {
        // BundleHelper checks if these bundles are already registered
        Bundle bundle = new Bundle();
        bundle.setConfig("{}");
        bundle.setState("{}");
        bundle.setName(BUNDLE);
        bundle.setStartup(BundleHelper.getDefaultBundleStartup("", BUNDLE, "seutumaisa-search"));
        BundleHelper.registerBundle(bundle);

        long viewId = VIEW_SERVICE.getDefaultViewIdForRole(ROLE_SEUTUMAISA);
        if(FlywayHelper.getBundleFromView(connection, BUNDLE, viewId) == null) {
            FlywayHelper.addBundleWithDefaults(connection, viewId, BUNDLE);
        }
    }
}
