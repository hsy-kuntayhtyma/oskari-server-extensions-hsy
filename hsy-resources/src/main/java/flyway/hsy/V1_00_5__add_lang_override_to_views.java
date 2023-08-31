package flyway.hsy;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.oskari.helpers.AppSetupHelper;

public class V1_00_5__add_lang_override_to_views extends BaseJavaMigration {

    private static final  String LANG_OVERRIDES = "hsy-lang-overrides";

    public void migrate(Context context) throws Exception {
        AppSetupHelper.addBundleToApps(context.getConnection(), LANG_OVERRIDES);
    }

}
