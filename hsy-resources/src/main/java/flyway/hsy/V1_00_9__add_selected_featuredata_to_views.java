package flyway.hsy;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.oskari.helpers.AppSetupHelper;

public class V1_00_9__add_selected_featuredata_to_views extends BaseJavaMigration {
	
	private static final String SELECTED_FEATUREDATA = "selected-featuredata";
	
	public void migrate(Context context) throws Exception {
        AppSetupHelper.addBundleToApps(context.getConnection(), SELECTED_FEATUREDATA);
    }

}
