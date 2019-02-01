package flyway.seutumaisa;

import fi.nls.oskari.db.BundleHelper;
import fi.nls.oskari.domain.map.view.Bundle;
import org.flywaydb.core.api.migration.jdbc.JdbcMigration;

import java.sql.Connection;

public class V1_03_3__register_map_location_bundle implements JdbcMigration {

    private static final String NAMESPACE = "hsy";
    private static final String MAP_LOCATION = "map-location";

    public void migrate(Connection connection) {
        // BundleHelper checks if these bundles are already registered
        Bundle linkPanel = new Bundle();
        linkPanel.setConfig("{}");
        linkPanel.setState("{}");
        linkPanel.setName(MAP_LOCATION);
        linkPanel.setStartup(BundleHelper.getDefaultBundleStartup(NAMESPACE, MAP_LOCATION, "Map location"));
        BundleHelper.registerBundle(linkPanel);
    }
}