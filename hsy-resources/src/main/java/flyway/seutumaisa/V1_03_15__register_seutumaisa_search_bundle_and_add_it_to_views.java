package flyway.seutumaisa;

import fi.nls.oskari.db.BundleHelper;
import fi.nls.oskari.domain.map.view.Bundle;
import fi.nls.oskari.map.view.ViewService;
import fi.nls.oskari.map.view.ViewServiceIbatisImpl;
import fi.nls.oskari.util.FlywayHelper;
import org.flywaydb.core.api.migration.jdbc.JdbcMigration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class V1_03_15__register_seutumaisa_search_bundle_and_add_it_to_views implements JdbcMigration {
    private static final String NAMESPACE = "seutumaisa";
    private static final String BUNDLE = "seutumaisa-search";
    private static final ViewService VIEW_SERVICE = new ViewServiceIbatisImpl();
    private static final String ROLE_SEUTUMAISA = "seutumaisa";

    public void migrate(Connection connection) throws SQLException {
        // BundleHelper checks if these bundles are already registered
        Bundle bundle = new Bundle();
        bundle.setConfig("{}");
        bundle.setState("{}");
        bundle.setName(BUNDLE);
        bundle.setStartup(null);

        if (!BundleHelper.isBundleRegistered(bundle.getName(), connection)) {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO portti_bundle(name, startup, config, state) VALUES(?,null,?,?)");

            Throwable var3 = null;

            try {
                statement.setString(1, bundle.getName());
                statement.setString(2, bundle.getConfig());
                statement.setString(3, bundle.getState());
                statement.execute();
            } catch (Throwable var12) {
                var3 = var12;
                throw var12;
            } finally {
                if (statement != null) {
                    if (var3 != null) {
                        try {
                            statement.close();
                        } catch (Throwable var11) {
                            var3.addSuppressed(var11);
                        }
                    } else {
                        statement.close();
                    }
                }

            }
        }

        long viewId = VIEW_SERVICE.getDefaultViewIdForRole(ROLE_SEUTUMAISA);
        if(FlywayHelper.getBundleFromView(connection, BUNDLE, viewId) == null) {
            FlywayHelper.addBundleWithDefaults(connection, viewId, BUNDLE);
        }
    }
}
