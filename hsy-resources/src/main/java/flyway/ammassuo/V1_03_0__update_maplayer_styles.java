package flyway.ammassuo;

import fi.nls.oskari.domain.map.OskariLayer;
import fi.nls.oskari.log.LogFactory;
import fi.nls.oskari.log.Logger;
import fi.nls.oskari.map.layer.OskariLayerService;
import fi.nls.oskari.service.OskariComponentManager;
import fi.nls.oskari.util.PropertyUtil;
import org.flywaydb.core.api.migration.jdbc.JdbcMigration;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import fi.nls.oskari.util.IOHelper;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

public class V1_03_0__update_maplayer_styles implements JdbcMigration {
    private static final Logger LOG = LogFactory.getLogger(V1_03_0__update_maplayer_styles.class);
    private OskariLayerService layerService  = OskariComponentManager.getComponentOfType(OskariLayerService.class);

    private static String LAYER_DATA_URL  = PropertyUtil.get("geoserver.url", "http://localhost:8080/geoserver") + "/wfs";

    public void migrate(Connection connection) throws Exception {
        updateLayerStyles(connection);
    }

    private void updateLayerStyles(final Connection conn) throws IOException, JSONException {

        final String json = IOHelper.readString(getClass().getResourceAsStream("hsy-layer-styles.json"));
        JSONArray array = new JSONArray(json);

        for(int i = 0; i < array.length(); i++) {
            JSONObject obj = (JSONObject)array.get(i);
            String name = obj.get("name").toString();
            JSONObject style = (JSONObject)obj.get("style");
            List<OskariLayer> layers = layerService.findByUrlAndName(LAYER_DATA_URL, name);
            if (layers.isEmpty()) {
                LOG.warn(String.format("No matching layer found (url: %s name: %s), could not set style.", LAYER_DATA_URL, name));
            } else if (layers.size() > 1) {
                LOG.warn(String.format("Multiple layers found (url: %s name: %s), could not set style.", LAYER_DATA_URL, name));
            } else {
                updateLayerStyle(conn, layers.get(0).getId(), name, style.toString());
            }
        }
    }

    private static void updateLayerStyle(final Connection conn, final int layerId, final String styleName, final String style) {
        try {
            if(style != null) {
                final PreparedStatement statement = conn.prepareStatement("UPDATE oskari_maplayer SET style = ?, " +
                        "options = ? WHERE id = ?;");

                statement.setString(1, styleName);
                statement.setString(2, style);
                statement.setLong(3, layerId);

                try {
                    statement.execute();
                } finally {
                    statement.close();
                }
            }
        } catch (Exception ex) {
            LOG.error(ex, "Cannot update layer (id=" + layerId + ") style.");
        }
        LOG.info(String.format("Style set for layer (id=%d).", layerId));
    }
}
