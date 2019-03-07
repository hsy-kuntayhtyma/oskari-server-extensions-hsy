package flyway.seutumaisa;

import fi.nls.oskari.domain.map.view.Bundle;
import fi.nls.oskari.map.layer.OskariLayerService;
import fi.nls.oskari.map.layer.OskariLayerServiceIbatisImpl;
import fi.nls.oskari.map.view.ViewService;
import fi.nls.oskari.map.view.ViewServiceIbatisImpl;
import fi.nls.oskari.util.FlywayHelper;
import helpers.LayerHelper;
import org.flywaydb.core.api.migration.jdbc.JdbcMigration;
import org.json.JSONObject;

import java.sql.Connection;

public class V1_03_7__update_userguide_configuration  implements JdbcMigration {
    private static final ViewService VIEW_SERVICE = new ViewServiceIbatisImpl();
    private static final OskariLayerService LAYER_SERVICE = new OskariLayerServiceIbatisImpl();

    private static final String BUNDLE = "userguide";

    public void migrate(Connection connection) throws Exception {
        long viewId = VIEW_SERVICE.getDefaultViewIdForRole(LayerHelper.ROLE_SEUTUMAISA);
        Bundle bundle = FlywayHelper.getBundleFromView(connection, BUNDLE, viewId);
        JSONObject config = new JSONObject("{\"tabs\":[{\"title\":\"Karttaikkuna\",\"tags\":\"ohje_karttaikkuna\"},{\"title\":\"Työkalut\",\"tags\":\"ohje_tyokalut\"},{\"title\":\"Haku\",\"tags\":\"ohje_haku\"},{\"title\":\"Karttatasot\",\"tags\":\"ohje_karttatasot\"}]}");
        bundle.setConfig(config.toString(2));
        FlywayHelper.updateBundleInView(connection, bundle, viewId);

    }
}
