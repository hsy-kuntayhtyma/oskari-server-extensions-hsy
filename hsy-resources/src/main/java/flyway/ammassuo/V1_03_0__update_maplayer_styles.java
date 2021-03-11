package flyway.ammassuo;

import fi.nls.oskari.domain.map.OskariLayer;
import fi.nls.oskari.log.LogFactory;
import fi.nls.oskari.log.Logger;
import fi.nls.oskari.map.layer.OskariLayerService;
import fi.nls.oskari.util.PropertyUtil;
import org.flywaydb.core.api.migration.jdbc.JdbcMigration;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import fi.nls.oskari.util.IOHelper;
import java.io.IOException;
import java.sql.Connection;

public class V1_03_0__update_maplayer_styles implements JdbcMigration {
    private static final Logger LOG = LogFactory.getLogger(V1_03_0__update_maplayer_styles.class);
    private OskariLayerService layerService;
    private static final String SRS_3879 = "EPSG:3879";

    private static String LAYER_DATA_URL  = PropertyUtil.get("geoserver.url", "http://localhost:8080/geoserver") + "/wfs";

    public void migrate(Connection connection) throws Exception {
        updateLayerStyles(connection);
    }

    private void updateLayerStyles(final Connection conn) throws IOException, JSONException {

        final String json = IOHelper.readString(getClass().getResourceAsStream("hsy-layer-styles.json"));
        JSONArray array = new JSONArray(json);
        JSONArray layers = new JSONArray();

        for(int i = 0; i < array.length(); i++) {
            JSONObject obj = (JSONObject)array.get(i);
            String name = obj.get("name").toString();
            JSONObject style = (JSONObject)obj.get("style");
            OskariLayer layer = layerService.findByUrlAndName(LAYER_DATA_URL, name).get(0);
            layer.setStyle(String.valueOf(style));
        }
    }

}
