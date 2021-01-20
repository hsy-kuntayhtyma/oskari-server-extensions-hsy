package flyway.ammassuo;

import fi.nls.oskari.map.view.ViewService;
import fi.nls.oskari.map.view.AppSetupServiceMybatisImpl;
import fi.nls.oskari.util.FlywayHelper;
import org.flywaydb.core.api.migration.jdbc.JdbcMigration;

import java.sql.Connection;

public class V1_01_2__add_lang_override_to_ammassuo_view implements JdbcMigration {

    private static final ViewService VIEW_SERVICE = new AppSetupServiceMybatisImpl();
    private static final  String LANG_OVERRIDES = "hsy-lang-overrides";
    private static final String ROLE_AMMASSUO = "Ammassuo";

    public void migrate(Connection connection) throws Exception {
        long viewId = VIEW_SERVICE.getDefaultViewIdForRole(ROLE_AMMASSUO);
        if(FlywayHelper.getBundleFromView(connection, LANG_OVERRIDES, viewId) == null) {
            FlywayHelper.addBundleWithDefaults(connection, viewId, LANG_OVERRIDES);
        }
    }
}