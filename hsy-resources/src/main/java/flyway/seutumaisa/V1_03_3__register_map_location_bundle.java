package flyway.seutumaisa;

import fi.nls.oskari.db.BundleHelper;
import fi.nls.oskari.domain.map.view.Bundle;
import org.flywaydb.core.api.migration.jdbc.JdbcMigration;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class V1_03_3__register_map_location_bundle implements JdbcMigration {

    private static final String NAMESPACE = "hsy";
    private static final String MAP_LOCATION = "map-location";

    public void migrate(Connection connection) throws Exception {
        // BundleHelper checks if these bundles are already registered
        Bundle bundle = new Bundle();
        bundle.setConfig("{}");
        bundle.setState("{}");
        bundle.setName(MAP_LOCATION);
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
    }



}