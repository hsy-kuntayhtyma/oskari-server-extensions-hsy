package flyway.hsy;

import fi.nls.oskari.domain.map.view.View;
import fi.nls.oskari.map.view.ViewService;
import fi.nls.oskari.map.view.AppSetupServiceMybatisImpl;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.oskari.helpers.AppSetupHelper;

import java.sql.Connection;
import java.util.List;

public class V1_02_4__add_selected_featuredata_to_views extends BaseJavaMigration {
    private static final ViewService VIEW_SERVICE = new AppSetupServiceMybatisImpl();
    private static final  String BUNDLE_ID = "selected-featuredata";

    public void migrate(Context context) throws Exception {
        Connection connection = context.getConnection();
        List<View> views = VIEW_SERVICE.getViewsForUser(-1);
        for (View v : views) {
            if (v.isDefault()) {
                if (!AppSetupHelper.appContainsBundle(connection, v.getId(), BUNDLE_ID)) {
                    AppSetupHelper.addBundleToApp(connection, v.getId(), BUNDLE_ID);
                }
            }
        }
    }
}
