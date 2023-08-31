package flyway.hsy;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.oskari.helpers.AppSetupHelper;

public class V1_00_1__add_download_basket_to_views extends BaseJavaMigration {
	
	private static final  String DOWNLOAD_BASKET = "download-basket";
	
	public void migrate(Context context) throws Exception {
		AppSetupHelper.addBundleToApps(context.getConnection(), DOWNLOAD_BASKET);
    }

}
