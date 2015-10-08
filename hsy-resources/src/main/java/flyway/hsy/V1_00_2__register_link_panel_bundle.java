package flyway.hsy;

import java.sql.Connection;

import org.flywaydb.core.api.migration.jdbc.JdbcMigration;

import fi.nls.oskari.db.BundleHelper;
import fi.nls.oskari.domain.map.view.Bundle;

public class V1_00_2__register_link_panel_bundle implements JdbcMigration{
	
	private static final String NAMESPACE = "hsy";
	private static final String LINK_PANEL = "link-panel";

	public void migrate(Connection connection) {
	// BundleHelper checks if these bundles are already registered
		Bundle linkPanel = new Bundle();
		linkPanel.setConfig("{}");
		linkPanel.setState("{}");
		linkPanel.setName(LINK_PANEL);
		linkPanel.setStartup(BundleHelper.getDefaultBundleStartup(NAMESPACE, LINK_PANEL, "Link panel"));
		BundleHelper.registerBundle(linkPanel);
	}
}
