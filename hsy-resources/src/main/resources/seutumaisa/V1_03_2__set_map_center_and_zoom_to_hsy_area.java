package flyway.seutumaisa;

import fi.nls.oskari.domain.map.view.Bundle;
import fi.nls.oskari.map.layer.OskariLayerService;
import fi.nls.oskari.map.layer.OskariLayerServiceMybatisImpl;
import fi.nls.oskari.map.view.ViewService;
import fi.nls.oskari.map.view.AppSetupServiceMybatisImpl;
import fi.nls.oskari.util.FlywayHelper;
import fi.nls.oskari.util.PropertyUtil;
import org.flywaydb.core.api.migration.jdbc.JdbcMigration;
import org.json.JSONObject;

import java.sql.Connection;

public class V1_03_2__set_map_center_and_zoom_to_hsy_area implements JdbcMigration
{
    private static final ViewService VIEW_SERVICE = new AppSetupServiceMybatisImpl();
    private static final OskariLayerService LAYER_SERVICE = new OskariLayerServiceMybatisImpl();

    private static final String ROLE_SEUTUMAISA = "SeutuMaisa";
    private static final String MAPFULL = "mapfull";
    private static final String KEY_EAST = "east";
    private static final String KEY_NORTH = "north";
    private static final String KEY_ZOOM = "zoom";

    public void migrate(Connection connection) throws Exception {
        long viewId = VIEW_SERVICE.getDefaultViewIdForRole(ROLE_SEUTUMAISA);
        Bundle mapfull = FlywayHelper.getBundleFromView(connection, MAPFULL, viewId);
        JSONObject state = mapfull.getStateJSON();
        if(state.has(KEY_EAST)) {
            state.remove(KEY_EAST);
        }
        if(state.has(KEY_NORTH)) {
            state.remove(KEY_NORTH);
        }
        if(state.has(KEY_ZOOM)) {
            state.remove(KEY_ZOOM);
        }
        state.put(KEY_EAST, Double.parseDouble(PropertyUtil.get("flyway.seutumaisa.V1_03_2.east","25494439")));
        state.put(KEY_NORTH, Double.parseDouble(PropertyUtil.get("flyway.seutumaisa.V1_03_2.east","6676085")));
        state.put(KEY_ZOOM, Integer.parseInt(PropertyUtil.get("flyway.seutumaisa.V1_03_2.zoom","3")));
        mapfull.setState(state.toString(2));
        FlywayHelper.updateBundleInView(connection,mapfull, viewId);
    }
}