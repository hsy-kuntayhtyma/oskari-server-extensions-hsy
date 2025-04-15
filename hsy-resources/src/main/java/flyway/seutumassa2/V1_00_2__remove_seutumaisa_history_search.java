package flyway.seutumassa2;

import java.sql.Connection;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.oskari.helpers.AppSetupHelper;

public class V1_00_2__remove_seutumaisa_history_search extends BaseJavaMigration {

    private static final String SEUTUMAISA_HISTORY_SEARCH = "seutumaisa-history-search";

    public void migrate(Context context) throws Exception {
        Connection c = context.getConnection();

        for (long appsetupId : AppSetupHelper.getSetupsForType(c)) {
            if (AppSetupHelper.appContainsBundle(c, appsetupId, SEUTUMAISA_HISTORY_SEARCH)) {
                AppSetupHelper.removeBundleFromApp(c, appsetupId, SEUTUMAISA_HISTORY_SEARCH);
            }
        }
    }

}
