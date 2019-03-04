package helpers;

import fi.nls.oskari.domain.map.view.Bundle;
import fi.nls.oskari.domain.map.view.View;
import fi.nls.oskari.map.view.ViewException;
import fi.nls.oskari.map.view.ViewService;
import fi.nls.oskari.map.view.ViewServiceIbatisImpl;
import fi.nls.oskari.map.view.util.ViewHelper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.util.List;

/***
 * @author Marko Kuosmanen
 * This class helps for anything of layers
 */
public class LayerHelper {
    public static final String ROLE_SEUTUMAISA = "SeutuMaisa";
    public static final String ROLE_AMMASSUO = "Ammassuo";

    private static final ViewService VIEW_SERVICE = new ViewServiceIbatisImpl();
    private static final String BUNDLE_MAPFULL = "mapfull";
    private static final String BACKGROUND_LAYER_SELECTION_PLUGIN = "Oskari.mapframework.bundle.mapmodule.plugin.BackgroundLayerSelectionPlugin";

    /**
     * Sets background layer selection plugin layers
     *
     * @param connection sql connection
     * @param layerIds   layerids
     */
    public static void SetBackgroundSelectionPluginLayers(final Connection connection, final String[] layerIds) throws ViewException, JSONException {

        if(layerIds == null || layerIds.length == 0) {
            return;
        }

        List<View> views = VIEW_SERVICE.getViewsForUser(-1);
        for (View v : views) {
            if (v.isDefault()) {
                final Bundle mapfull = v.getBundleByName(BUNDLE_MAPFULL);

                final JSONObject config = mapfull.getConfigJSON();
                final JSONArray plugins = config.optJSONArray("plugins");

                for (int i = 0; i < plugins.length(); ++i) {
                    JSONObject plugin = plugins.getJSONObject(i);
                    if (BACKGROUND_LAYER_SELECTION_PLUGIN.equals(plugin.optString("id"))) {
                        final JSONObject pluginConfig = plugin.optJSONObject("config");
                        if (pluginConfig != null) {
                            JSONArray baseLayers = new JSONArray();
                            for (String layerId : layerIds) {
                                baseLayers.put(layerId);
                            }
                            pluginConfig.remove("baseLayers");
                            pluginConfig.put("baseLayers", baseLayers);
                            break;
                        }

                    }
                }

                VIEW_SERVICE.updateBundleSettingsForView(v.getId(), mapfull);
            }
        }

    }
}
