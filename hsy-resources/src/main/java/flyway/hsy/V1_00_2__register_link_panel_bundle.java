package flyway.hsy;

import java.sql.SQLException;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.oskari.helpers.BundleHelper;

import fi.nls.oskari.domain.map.view.Bundle;

public class V1_00_2__register_link_panel_bundle extends BaseJavaMigration {
	
	private static final String LINK_PANEL = "link-panel";

    public void migrate(Context context) throws SQLException {
		Bundle linkPanel = new Bundle();
		linkPanel.setConfig("{}");
		linkPanel.setState("{}");
		linkPanel.setName(LINK_PANEL);
		BundleHelper.registerBundle(context.getConnection(), linkPanel);
	}
}
