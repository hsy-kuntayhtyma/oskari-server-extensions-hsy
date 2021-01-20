package flyway.seutumaisa;

import fi.nls.oskari.domain.map.view.Bundle;
import fi.nls.oskari.map.view.ViewService;
import fi.nls.oskari.map.view.AppSetupServiceMybatisImpl;
import fi.nls.oskari.util.FlywayHelper;
import org.flywaydb.core.api.migration.jdbc.JdbcMigration;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class V1_03_4__add_map_location_to_seutumaisa_view  implements JdbcMigration {

    private static final ViewService VIEW_SERVICE = new AppSetupServiceMybatisImpl();
    private static final  String BUNDLE_ID = "map-location";

    private static final  String KEY_X = "x";
    private static final  String KEY_Y = "y";
    private static final  String KEY_ZOOM = "zoom";


    public void migrate(Connection connection) throws Exception {
        final List<Long> views =getViews(connection);
        for(Long viewId : views){
            if (FlywayHelper.viewContainsBundle(connection, BUNDLE_ID, viewId)) {
                continue;
            }
            FlywayHelper.addBundleWithDefaults(connection, viewId, BUNDLE_ID);

            Bundle mapLocation = FlywayHelper.getBundleFromView(connection, BUNDLE_ID, viewId);
            JSONObject config = new JSONObject();
            config.put(KEY_X, 25494439);
            config.put(KEY_Y, 6676085);
            config.put(KEY_ZOOM, 3);
            mapLocation.setConfig(config.toString(4));
            FlywayHelper.updateBundleInView(connection, mapLocation, viewId);
        }
    }

    private ArrayList<Long> getViews(Connection connection) throws Exception {
        ArrayList<Long> ids = new ArrayList<>();

        final PreparedStatement statement =
                connection.prepareStatement("SELECT id FROM portti_view " +
                        "WHERE type='DEFAULT' AND name='SeutuMaisa'");
        try (ResultSet rs = statement.executeQuery()) {
            while(rs.next()) {
                ids.add(rs.getLong("id"));
            }
        } finally {
            statement.close();
        }
        return ids;
    }


}