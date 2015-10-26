package flyway.hsy;

import java.sql.Connection;

import org.flywaydb.core.api.migration.jdbc.JdbcMigration;

import fi.nls.oskari.db.BundleHelper;
import fi.nls.oskari.domain.map.view.Bundle;

public class V1_00_4__register_lang_override_bundle implements JdbcMigration{
	private static final String NAMESPACE = "hsy";
	private static final String LANG_OVERRIDES = "hsy-lang-overrides";

	public void migrate(Connection connection) {
	// BundleHelper checks if these bundles are already registered
		Bundle linkPanel = new Bundle();
		linkPanel.setConfig("{}");
		linkPanel.setState("{}");
		linkPanel.setName(LANG_OVERRIDES);
		linkPanel.setStartup(BundleHelper.getDefaultBundleStartup(NAMESPACE, LANG_OVERRIDES, "Lang overrides"));
		BundleHelper.registerBundle(linkPanel);
	}
}
