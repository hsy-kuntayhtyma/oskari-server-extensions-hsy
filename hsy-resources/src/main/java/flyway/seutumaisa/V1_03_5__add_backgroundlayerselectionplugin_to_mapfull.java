package flyway.seutumaisa;

import fi.nls.oskari.domain.map.OskariLayer;
import fi.nls.oskari.domain.map.view.Bundle;
import fi.nls.oskari.domain.map.view.View;
import fi.nls.oskari.map.layer.OskariLayerService;
import fi.nls.oskari.map.layer.OskariLayerServiceMybatisImpl;
import fi.nls.oskari.map.view.ViewService;
import fi.nls.oskari.map.view.AppSetupServiceMybatisImpl;
import org.flywaydb.core.api.migration.jdbc.JdbcMigration;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.util.List;

public class V1_03_5__add_backgroundlayerselectionplugin_to_mapfull implements JdbcMigration {
    private static final ViewService VIEW_SERVICE = new AppSetupServiceMybatisImpl();
    private static final OskariLayerService LAYER_SERVICE = new OskariLayerServiceMybatisImpl();
    private static final String PLUGIN_NAME = "Oskari.mapframework.bundle.mapmodule.plugin.BackgroundLayerSelectionPlugin";
    private static final String MAPFULL = "mapfull";
    private static final String OPASKARTTA_NAME = "avoindata:Opaskartta_PKS";
    private static final String ORTOILMAKUVA2015_NAME = "taustakartat_ja_aluejaot:ortoilmakuva2015";
    private static final String OSM_NAME = "osm-finland";
    public void migrate(Connection connection)
            throws Exception {
        List<View> views = VIEW_SERVICE.getViewsForUser(-1);
        for(View v : views) {
            if(v.isDefault()) {
                final Bundle mapfull = v.getBundleByName(MAPFULL);
                boolean addedPlugin = addPlugin(mapfull);
                if(addedPlugin) {
                    VIEW_SERVICE.updateBundleSettingsForView(v.getId(), mapfull);
                }
            }
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
                        || (ORTOILMAKUVA2015_NAME.equals(layer.getName())
                        || (OSM_NAME.equals(layer.getName())))
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