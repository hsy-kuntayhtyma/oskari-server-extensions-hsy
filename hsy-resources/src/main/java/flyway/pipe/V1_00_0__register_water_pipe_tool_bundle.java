package flyway.pipe;

import java.sql.Connection;

import org.flywaydb.core.api.migration.jdbc.JdbcMigration;

import fi.nls.oskari.db.BundleHelper;
import fi.nls.oskari.domain.map.view.Bundle;

public class V1_00_0__register_water_pipe_tool_bundle implements JdbcMigration{

	private static final String NAMESPACE = "hsy";
	private static final String WATER_PIPE_TOOL = "water-pipe-tool";

	public void migrate(Connection connection) {
	// BundleHelper checks if these bundles are already registered
		Bundle waterPipeTool = new Bundle();
		waterPipeTool.setConfig("{}");
		waterPipeTool.setState("{}");
		waterPipeTool.setName(WATER_PIPE_TOOL);
		waterPipeTool.setStartup(BundleHelper.getDefaultBundleStartup(NAMESPACE, WATER_PIPE_TOOL, "Waterpipe tool"));
		BundleHelper.registerBundle(waterPipeTool);
	}
}
