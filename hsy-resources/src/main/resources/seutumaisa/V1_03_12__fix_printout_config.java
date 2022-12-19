package flyway.seutumaisa;

import fi.nls.oskari.domain.map.view.Bundle;
import fi.nls.oskari.map.layer.OskariLayerService;
import fi.nls.oskari.map.layer.OskariLayerServiceMybatisImpl;
import fi.nls.oskari.map.view.ViewService;
import fi.nls.oskari.map.view.AppSetupServiceMybatisImpl;
import fi.nls.oskari.util.FlywayHelper;
import helpers.LayerHelper;
import org.flywaydb.core.api.migration.jdbc.JdbcMigration;
import org.json.JSONObject;

import java.sql.Connection;

public class V1_03_12__fix_printout_config implements JdbcMigration {
    private static final ViewService VIEW_SERVICE = new AppSetupServiceMybatisImpl();
    private static final OskariLayerService LAYER_SERVICE = new OskariLayerServiceMybatisImpl();

    private static final String BUNDLE = "printout";

    public void migrate(Connection connection) throws Exception {
        long viewId = VIEW_SERVICE.getDefaultViewIdForRole(LayerHelper.ROLE_SEUTUMAISA);
        Bundle bundle = FlywayHelper.getBundleFromView(connection, BUNDLE, viewId);
        JSONObject config = new JSONObject("{}");
        bundle.setConfig(config.toString(2));
        FlywayHelper.updateBundleInView(connection, bundle, viewId);

    }
}
