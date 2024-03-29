package flyway.ammassuo;

import fi.nls.oskari.domain.map.view.Bundle;
import fi.nls.oskari.domain.map.view.View;
import fi.nls.oskari.map.layer.OskariLayerService;
import fi.nls.oskari.map.layer.OskariLayerServiceMybatisImpl;
import fi.nls.oskari.map.view.AppSetupServiceMybatisImpl;
import fi.nls.oskari.map.view.ViewService;
import org.flywaydb.core.api.migration.jdbc.JdbcMigration;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.util.List;

public class V1_03_1__update_logoplugin_config implements JdbcMigration {
    private static final ViewService VIEW_SERVICE = new AppSetupServiceMybatisImpl();
    private static final OskariLayerService LAYER_SERVICE = new OskariLayerServiceMybatisImpl();
    private static final String PLUGIN_NAME = "Oskari.mapframework.bundle.mapmodule.plugin.LogoPlugin";
    private static final String MAPFULL = "mapfull";

    public void migrate(Connection connection)
            throws Exception {
        List<View> views = VIEW_SERVICE.getViewsForUser(-1);
        for(View v : views) {
            if(v.isDefault()) {
                final Bundle mapfull = v.getBundleByName(MAPFULL);
                boolean updatedPlugin = updatePlugin(mapfull);
                if(updatedPlugin) {
                    VIEW_SERVICE.updateBundleSettingsForView(v.getId(), mapfull);
                }
            }
        }
    }

    private boolean updatePlugin(final Bundle mapfull) throws JSONException {
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
                plugins.remove(i);
                break;
            }
        }

        // Update plugin config if found
        if(found) {
            JSONObject plugin = new JSONObject();
            plugin.put("id", PLUGIN_NAME);
            JSONObject pluginConfig = new JSONObject();

            JSONObject terms = new JSONObject();
            terms.put("fi", "https://www.hsy.fi/ilmanlaatu-ja-ilmasto/paikkatiedot/avoin-karttapalvelu/");
            terms.put("en", "https://www.hsy.fi/en/air-quality-and-climate/geographic-information/open-map-service/");
            terms.put("sv", "https://www.hsy.fi/sv/luftkvalitet-och-klimat/geodata/oppen-karttjanst/");

            pluginConfig.put("termsUrl", terms);

            plugin.put("config", pluginConfig);
            plugins.put(plugin);
        }
        return true;
    }

}