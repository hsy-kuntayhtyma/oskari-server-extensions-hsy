package flyway.hsy;

import java.sql.Connection;

import org.flywaydb.core.api.migration.jdbc.JdbcMigration;

import fi.nls.oskari.db.BundleHelper;
import fi.nls.oskari.domain.map.view.Bundle;

public class V1_00_6__register_content_editor_bundle implements JdbcMigration{

	private static final String NAMESPACE = "tampere";
	private static final String CONTENT_EDITOR = "content-editor";

	public void migrate(Connection connection) {
	// BundleHelper checks if these bundles are already registered
		Bundle contenEditorTool = new Bundle();
		contenEditorTool.setConfig("{}");
		contenEditorTool.setState("{}");
		contenEditorTool.setName(CONTENT_EDITOR);
		contenEditorTool.setStartup(BundleHelper.getDefaultBundleStartup(NAMESPACE, CONTENT_EDITOR, "content-editor"));
		BundleHelper.registerBundle(contenEditorTool);
	}
}
