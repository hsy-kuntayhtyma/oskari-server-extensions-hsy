package flyway.hsy;

import java.sql.SQLException;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.oskari.helpers.BundleHelper;

import fi.nls.oskari.domain.map.view.Bundle;

public class V1_00_8__register_selected_featuredata_bundle extends BaseJavaMigration {

	private static final String SELECTED_FEATUREDATA = "selected-featuredata";

	public void migrate(Context context) throws SQLException {
		Bundle selectedFeatureData = new Bundle();
		selectedFeatureData.setConfig("{}");
		selectedFeatureData.setState("{}");
		selectedFeatureData.setName(SELECTED_FEATUREDATA);
		BundleHelper.registerBundle(context.getConnection(), selectedFeatureData);
	}
}
