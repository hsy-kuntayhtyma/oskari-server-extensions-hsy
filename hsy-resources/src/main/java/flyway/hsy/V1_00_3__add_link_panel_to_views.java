package flyway.hsy;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.oskari.helpers.AppSetupHelper;

public class V1_00_3__add_link_panel_to_views extends BaseJavaMigration {
	
	private static final String LINK_PANEL = "link-panel";
	
	public void migrate(Context context) throws Exception {
       AppSetupHelper.addBundleToApps(context.getConnection(), LINK_PANEL);
	}
	
}
