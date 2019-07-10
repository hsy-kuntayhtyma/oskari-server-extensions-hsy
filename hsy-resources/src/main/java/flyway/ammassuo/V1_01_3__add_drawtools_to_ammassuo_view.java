package flyway.ammassuo;

import fi.nls.oskari.map.view.ViewService;
import fi.nls.oskari.map.view.ViewServiceIbatisImpl;
import fi.nls.oskari.util.FlywayHelper;
import org.flywaydb.core.api.migration.jdbc.JdbcMigration;

import java.sql.Connection;

public class V1_01_3__add_drawtools_to_ammassuo_view implements JdbcMigration {

    private static final ViewService VIEW_SERVICE = new ViewServiceIbatisImpl();
    private static final  String DRAWTOOLS = "drawtools";
    private static final String ROLE_AMMASSUO = "Ammassuo";

    public void migrate(Connection connection) throws Exception {
        long viewId = VIEW_SERVICE.getDefaultViewIdForRole(ROLE_AMMASSUO);
        if(FlywayHelper.getBundleFromView(connection, DRAWTOOLS, viewId) == null) {
            FlywayHelper.addBundleWithDefaults(connection, viewId, DRAWTOOLS);
        }
    }
}