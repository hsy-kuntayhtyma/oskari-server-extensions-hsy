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
import org.flywaydb.core.api.migration.jdbc.JdbcMigration;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static fi.nls.oskari.view.modifier.ViewModifier.BUNDLE_MAPFULL;

public class V1_03_6__mappfull_add_selected_layers implements JdbcMigration {
    private static final Logger LOG = LogFactory.getLogger(V1_03_6__mappfull_add_selected_layers.class);
    private static final ViewService VIEW_SERVICE = new ViewServiceIbatisImpl();
    private static final OskariLayerService LAYER_SERVICE = new OskariLayerServiceIbatisImpl();
    private static final String PLUGIN_NAME = "Oskari.mapframework.bundle.mapmodule.plugin.BackgroundLayerSelectionPlugin";
    private static final String MAPFULL = "mapfull";
    private static final String OPASKARTTA_NAME = "avoindata:Opaskartta_PKS";
    private static final String KEY_SELECTED_LAYERS = "selectedLayers";

    private ViewService service = null;
    private int updatedViewCount = 0;


    public void migrate(Connection connection)
            throws Exception {
        service =  new ViewServiceIbatisImpl();
        try {
            updateViews(connection);
        }
        finally {
            LOG.info("Updated views:", updatedViewCount);
            service = null;
        }
    }

    private void updateViews(Connection conn)
            throws Exception {
        List<View> list = getViews(conn);
        for(View view : list) {
            View modifyView = service.getViewWithConf(view.getId());

            final Bundle mapfull = modifyView.getBundleByName(BUNDLE_MAPFULL);
            boolean modified = modifySelectedLayers(mapfull);
            if(modified) {
                service.updateBundleSettingsForView(view.getId(), mapfull);
                updatedViewCount++;
            }
        }
    }

    private boolean modifySelectedLayers(final Bundle mapfull) throws JSONException {
        final JSONObject state = mapfull.getStateJSON();
        final JSONObject config = mapfull.getConfigJSON();
        List<OskariLayer> layers = LAYER_SERVICE.findAll();
        JSONArray selectedLayers = new JSONArray();

        for (int i = 0; i < layers.size(); i++) {
            OskariLayer layer = layers.get(i);
            if(OPASKARTTA_NAME.equals(layer.getName())) {
                JSONObject l = new JSONObject();
                l.put("id", layer.getId());
                selectedLayers.put(l);
            }
        }
        if(state.has(KEY_SELECTED_LAYERS)){
            state.remove(KEY_SELECTED_LAYERS);
        }


        state.put(KEY_SELECTED_LAYERS, selectedLayers);

        return true;
    }

    private List<View> getViews(Connection conn) throws SQLException {
        List<View> list = new ArrayList<>();
        final String sql = "SELECT distinct view_id " +
                "FROM portti_view_bundle_seq " +
                "WHERE bundle_id IN (SELECT id FROM portti_bundle WHERE name = 'mapfull') " +
                "AND view_id IN (SELECT id FROM portti_view WHERE type='DEFAULT' AND name='SeutuMaisa');";

        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    View view = new View();
                    view.setId(rs.getLong("view_id"));
                    list.add(view);
                }
            }
        }
        return list;
    }

}