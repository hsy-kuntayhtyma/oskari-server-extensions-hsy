package flyway.pipe;

import java.sql.SQLException;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.oskari.helpers.BundleHelper;

import fi.nls.oskari.domain.map.view.Bundle;

public class V1_00_0__register_water_pipe_tool_bundle extends BaseJavaMigration {

	private static final String WATER_PIPE_TOOL = "water-pipe-tool";

	public void migrate(Context context) throws SQLException {
	// BundleHelper checks if these bundles are already registered
		Bundle waterPipeTool = new Bundle();
		waterPipeTool.setConfig("{}");
		waterPipeTool.setState("{}");
		waterPipeTool.setName(WATER_PIPE_TOOL);
		BundleHelper.registerBundle(context.getConnection(), waterPipeTool);
	}
}
