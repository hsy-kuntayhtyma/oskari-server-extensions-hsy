package flyway.ammassuo;

import fi.nls.oskari.map.view.ViewService;
import fi.nls.oskari.map.view.ViewServiceIbatisImpl;
import fi.nls.oskari.util.FlywayHelper;
import org.flywaydb.core.api.migration.jdbc.JdbcMigration;

import java.sql.Connection;

public class V1_01_1__add_link_panel_to_ammassuo_view implements JdbcMigration {

    private static final ViewService VIEW_SERVICE = new ViewServiceIbatisImpl();
    private static final  String LINK_PANEL = "link-panel";
    private static final String ROLE_AMMASSUO = "Ammassuo";

    public void migrate(Connection connection) throws Exception {
        long viewId = VIEW_SERVICE.getDefaultViewIdForRole(ROLE_AMMASSUO);
        if(FlywayHelper.getBundleFromView(connection, LINK_PANEL, viewId) == null) {
            FlywayHelper.addBundleWithDefaults(connection, viewId, LINK_PANEL);
        }
    }
}