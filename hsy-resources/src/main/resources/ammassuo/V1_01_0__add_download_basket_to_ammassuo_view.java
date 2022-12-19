package flyway.ammassuo;

import fi.nls.oskari.map.view.ViewService;
import fi.nls.oskari.map.view.AppSetupServiceMybatisImpl;
import fi.nls.oskari.util.FlywayHelper;
import org.flywaydb.core.api.migration.jdbc.JdbcMigration;

import java.sql.Connection;

public class V1_01_0__add_download_basket_to_ammassuo_view implements JdbcMigration {

    private static final ViewService VIEW_SERVICE = new AppSetupServiceMybatisImpl();
    private static final  String DOWNLOAD_BASKET = "download-basket";
    private static final String ROLE_AMMASSUO = "Ammassuo";

    public void migrate(Connection connection) throws Exception {
        long viewId = VIEW_SERVICE.getDefaultViewIdForRole(ROLE_AMMASSUO);
        if(FlywayHelper.getBundleFromView(connection, DOWNLOAD_BASKET, viewId) == null) {
            FlywayHelper.addBundleWithDefaults(connection, viewId, DOWNLOAD_BASKET);
        }
    }
}