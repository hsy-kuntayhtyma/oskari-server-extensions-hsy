package flyway.seutumaisa;

import fi.nls.oskari.map.view.ViewService;
import fi.nls.oskari.map.view.AppSetupServiceMybatisImpl;
import fi.nls.oskari.util.FlywayHelper;
import org.flywaydb.core.api.migration.jdbc.JdbcMigration;

import java.sql.Connection;

public class V1_03_18__add_drawtools_to_views implements JdbcMigration {

    private static final ViewService VIEW_SERVICE = new AppSetupServiceMybatisImpl();
    private static final  String BUNDLE = "drawtools";
    private static final String ROLE = "seutumaisa";

    public void migrate(Connection connection) throws Exception {
        long viewId = VIEW_SERVICE.getDefaultViewIdForRole(ROLE);
        if(FlywayHelper.getBundleFromView(connection, BUNDLE, viewId) == null) {
            FlywayHelper.addBundleWithDefaults(connection, viewId, BUNDLE);
        }
    }
}