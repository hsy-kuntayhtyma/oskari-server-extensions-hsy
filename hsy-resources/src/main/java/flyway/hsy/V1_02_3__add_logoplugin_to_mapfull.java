package flyway.hsy;

import fi.nls.oskari.domain.map.view.Bundle;
import fi.nls.oskari.domain.map.view.View;
import fi.nls.oskari.map.view.ViewService;
import fi.nls.oskari.map.view.AppSetupServiceMybatisImpl;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class V1_02_3__add_logoplugin_to_mapfull extends BaseJavaMigration {
    private static final ViewService VIEW_SERVICE = new AppSetupServiceMybatisImpl();
    private static final String PLUGIN_NAME = "Oskari.mapframework.bundle.mapmodule.plugin.LogoPlugin";
    private static final String MAPFULL = "mapfull";
    public void migrate(Context context)
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

            JSONObject terms = new JSONObject();
            terms.put("fi", "https://www.hsy.fi/fi/asiantuntijalle/avoindata/karttapalvelu/Sivut/Karttapalvelun-käyttöehdot.aspx");
            terms.put("en", "https://www.hsy.fi/fi/asiantuntijalle/avoindata/karttapalvelu/Sivut/Karttapalvelun-käyttöehdot.aspx");
            terms.put("sv", "https://www.hsy.fi/fi/asiantuntijalle/avoindata/karttapalvelu/Sivut/Karttapalvelun-käyttöehdot.aspx");

            pluginConfig.put("termsUrl", terms);

            plugin.put("config", pluginConfig);
            plugins.put(plugin);
        }
        return true;
    }

}