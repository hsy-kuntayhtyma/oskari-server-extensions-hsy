package flyway.hsy;

import java.sql.Connection;

import org.flywaydb.core.api.migration.jdbc.JdbcMigration;

import fi.nls.oskari.db.BundleHelper;
import fi.nls.oskari.domain.map.view.Bundle;

public class V1_00_8__register_selected_featuredata_bundle implements JdbcMigration{

	private static final String NAMESPACE = "framework";
	private static final String SELECTED_FEATUREDATA = "selected-featuredata";

	public void migrate(Connection connection) {
	// BundleHelper checks if these bundles are already registered
		Bundle selectedFeatureData = new Bundle();
		selectedFeatureData.setConfig("{}");
		selectedFeatureData.setState("{}");
		selectedFeatureData.setName(SELECTED_FEATUREDATA);
		selectedFeatureData.setStartup(BundleHelper.getDefaultBundleStartup(NAMESPACE, SELECTED_FEATUREDATA, "Selected Featuredata"));
		BundleHelper.registerBundle(selectedFeatureData);
	}
}
