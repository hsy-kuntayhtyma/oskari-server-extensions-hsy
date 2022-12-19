package flyway.seutumaisa;

import java.sql.Connection;

import org.flywaydb.core.api.migration.jdbc.JdbcMigration;

import fi.nls.oskari.map.view.ViewService;
import fi.nls.oskari.map.view.AppSetupServiceMybatisImpl;
import fi.nls.oskari.util.FlywayHelper;

public class V1_03_14__add_timeseries_to_views implements JdbcMigration {
	
	private static final ViewService VIEW_SERVICE = new AppSetupServiceMybatisImpl();
    private static final  String TIMESERIES = "timeseries";
    private static final String ROLE_SEUTUMAISA = "seutumaisa";

    public void migrate(Connection connection) throws Exception {
        long viewId = VIEW_SERVICE.getDefaultViewIdForRole(ROLE_SEUTUMAISA);
        if(FlywayHelper.getBundleFromView(connection, TIMESERIES, viewId) == null) {
            FlywayHelper.addBundleWithDefaults(connection, viewId, TIMESERIES);
        }
    }
}
