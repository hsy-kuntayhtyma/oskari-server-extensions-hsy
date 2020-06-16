package flyway.seutumaisa;

import fi.nls.oskari.map.view.ViewService;
import fi.nls.oskari.map.view.AppSetupServiceMybatisImpl;
import fi.nls.oskari.util.FlywayHelper;
import helpers.LayerHelper;
import org.flywaydb.core.api.migration.jdbc.JdbcMigration;

import java.sql.Connection;

public class V1_03_11__add_selected_featuredata_to_view implements JdbcMigration {
    private static final ViewService VIEW_SERVICE = new AppSetupServiceMybatisImpl();
    private static final  String BUNDLE_ID = "selected-featuredata";

    public void migrate(Connection connection) throws Exception {

        long viewId = VIEW_SERVICE.getDefaultViewIdForRole(LayerHelper.ROLE_SEUTUMAISA);
        if (!FlywayHelper.viewContainsBundle(connection, BUNDLE_ID, viewId)) {
            FlywayHelper.addBundleWithDefaults(connection, viewId, BUNDLE_ID);
        }


    }
}
