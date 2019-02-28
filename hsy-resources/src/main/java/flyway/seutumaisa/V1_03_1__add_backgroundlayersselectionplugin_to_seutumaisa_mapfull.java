package flyway.seutumaisa;

import fi.nls.oskari.domain.map.OskariLayer;
import fi.nls.oskari.domain.map.view.Bundle;
import fi.nls.oskari.domain.map.view.View;
import fi.nls.oskari.log.LogFactory;
import fi.nls.oskari.log.Logger;
import fi.nls.oskari.map.layer.OskariLayerService;
import fi.nls.oskari.map.layer.OskariLayerServiceIbatisImpl;
import fi.nls.oskari.map.view.ViewService;
import fi.nls.oskari.map.view.ViewServiceIbatisImpl;
import helpers.LayerHelper;
import org.flywaydb.core.api.migration.jdbc.JdbcMigration;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.util.List;

public class V1_03_1__add_backgroundlayersselectionplugin_to_seutumaisa_mapfull implements JdbcMigration {
    private static final Logger LOG = LogFactory.getLogger(V1_03_1__add_backgroundlayersselectionplugin_to_seutumaisa_mapfull.class);
    private static final ViewService VIEW_SERVICE = new ViewServiceIbatisImpl();
    private static final OskariLayerService LAYER_SERVICE = new OskariLayerServiceIbatisImpl();
    private static final String PLUGIN_NAME = "Oskari.mapframework.bundle.mapmodule.plugin.BackgroundLayerSelectionPlugin";
    private static final String MAPFULL = "mapfull";
    private static final String OPASKARTTA_NAME = "Opaskartta_PKS";
    private static final String ORTOILMAKUVA2015_NAME = "taustakartat_ja_aluejaot:ortoilmakuva2015";

    public void migrate(Connection connection)
            throws Exception {

        long viewId = VIEW_SERVICE.getDefaultViewIdForRole(LayerHelper.ROLE_SEUTUMAISA);
        View view = VIEW_SERVICE.getViewWithConf(viewId);

        final Bundle mapfull = view.getBundleByName(MAPFULL);
        boolean addedPlugin = addPlugin(mapfull);
        if(addedPlugin) {
            VIEW_SERVICE.updateBundleSettingsForView(view.getId(), mapfull);
        }

    }

    private boolean addPlugin(final Bundle mapfull) throws JSONException {
        final JSONObject config = mapfull.getConfigJSON();
        final JSONArray plugins = config.optJSONArray("plugins");
        if(plugins == null) {
            throw new RuntimeException("No plugins" + config.toString(2));
        }
        boolean found = false;
        for(int i = 0; i < plugins.length(); ++i) {
            JSONObject plugin = plugins.getJSONObject(i);
            if(PLUGIN_NAME.equals(plugin.optString("id"))) {
                found = true;
                break;
            }
        }
        // add plugin if not there yet
        if(!found) {
            JSONObject plugin = new JSONObject();
            plugin.put("id", PLUGIN_NAME);
            JSONObject pluginConfig = new JSONObject();
            JSONArray baseLayers = new JSONArray();
            List<OskariLayer> layers = LAYER_SERVICE.findAll();
            for (int i = 0; i < layers.size(); i++) {
                OskariLayer layer = layers.get(i);
                if((OPASKARTTA_NAME.equals(layer.getName()))
                        || (ORTOILMAKUVA2015_NAME.equals(layer.getName()))
                        ){
                    baseLayers.put(Integer.toString(layer.getId()));
                }
            }

            pluginConfig.put("baseLayers", baseLayers);
            pluginConfig.put("showAsDropdown", false);
            plugin.put("config", pluginConfig);
            plugins.put(plugin);
        }
        return true;
    }
}