package flyway.ammassuo;

import fi.nls.oskari.domain.map.OskariLayer;
import fi.nls.oskari.domain.map.view.Bundle;
import fi.nls.oskari.log.LogFactory;
import fi.nls.oskari.log.Logger;
import fi.nls.oskari.map.layer.OskariLayerService;
import fi.nls.oskari.map.layer.OskariLayerServiceIbatisImpl;
import fi.nls.oskari.map.view.ViewService;
import fi.nls.oskari.map.view.ViewServiceIbatisImpl;
import fi.nls.oskari.util.FlywayHelper;
import helpers.LayerHelper;
import org.flywaydb.core.api.migration.jdbc.JdbcMigration;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Connection;
import java.util.List;

public class V1_00_3__set_selected_layers_ammassuo_view implements JdbcMigration
{
    private static final ViewService VIEW_SERVICE = new ViewServiceIbatisImpl();
    private static final OskariLayerService LAYER_SERVICE = new OskariLayerServiceIbatisImpl();
    private static final  String SELECTED_FEATUREDATA = "selected-featuredata";
    private static final String MAPFULL = "mapfull";
    private static final String KEY_ID = "id";
    private static final String KEY_OPACITY = "opacity";
    private static final String KEY_SELECTED_LAYERS = "selectedLayers";
    private static final String OPASKARTTA_NAME = "Opaskartta_PKS";

    public void migrate(Connection connection) throws Exception {
        long viewId = VIEW_SERVICE.getDefaultViewIdForRole(LayerHelper.ROLE_AMMASSUO);
        Bundle mapfull = FlywayHelper.getBundleFromView(connection, MAPFULL, viewId);
        JSONObject state = mapfull.getStateJSON();
        if(!state.has(KEY_SELECTED_LAYERS)) {
            JSONArray selectedLayers = new JSONArray();
            JSONObject opaskarttaLayer = new JSONObject();
            opaskarttaLayer.put(KEY_ID, getLayerId());
            opaskarttaLayer.put(KEY_OPACITY, 100);
            selectedLayers.put(opaskarttaLayer);
            state.put(KEY_SELECTED_LAYERS, selectedLayers);
            mapfull.setState(state.toString(2));
            FlywayHelper.updateBundleInView(connection, mapfull, viewId);
        }
    }

    private long getLayerId () {
        List<OskariLayer> layers = LAYER_SERVICE.findAll();
        for (int i = 0; i < layers.size(); i++) {
            OskariLayer layer = layers.get(i);
            if (OPASKARTTA_NAME.equals(layer.getName())){
                    return layer.getId();
            }
        }
        return -1;
    }
}

