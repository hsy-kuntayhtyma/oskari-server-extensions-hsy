package flyway.ammassuo;

import fi.nls.oskari.map.view.ViewService;
import fi.nls.oskari.map.view.ViewServiceIbatisImpl;
import fi.nls.oskari.util.FlywayHelper;
import org.flywaydb.core.api.migration.jdbc.JdbcMigration;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class V1_00_2__add_selected_featuredata_to_ammassuo_view implements JdbcMigration {

    private static final ViewService VIEW_SERVICE = new ViewServiceIbatisImpl();
    private static final  String SELECTED_FEATUREDATA = "selected-featuredata";
    private static final String ROLE_AMMASSUO = "Ammassuo";

    public void migrate(Connection connection) throws Exception {
        long viewId = VIEW_SERVICE.getDefaultViewIdForRole(ROLE_AMMASSUO);
        if(FlywayHelper.getBundleFromView(connection, SELECTED_FEATUREDATA, viewId) == null) {
            FlywayHelper.addBundleWithDefaults(connection, viewId, SELECTED_FEATUREDATA);
        }
    }
}