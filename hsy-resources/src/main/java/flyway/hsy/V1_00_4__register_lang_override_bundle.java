package flyway.hsy;

import java.sql.SQLException;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.oskari.helpers.BundleHelper;

import fi.nls.oskari.domain.map.view.Bundle;

public class V1_00_4__register_lang_override_bundle extends BaseJavaMigration {

    private static final String LANG_OVERRIDES = "hsy-lang-overrides";

	public void migrate(Context context) throws SQLException {
		Bundle linkPanel = new Bundle();
		linkPanel.setConfig("{}");
		linkPanel.setState("{}");
		linkPanel.setName(LANG_OVERRIDES);
		BundleHelper.registerBundle(context.getConnection(), linkPanel);
	}
}
