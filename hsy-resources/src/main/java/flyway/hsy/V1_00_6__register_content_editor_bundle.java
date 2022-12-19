package flyway.hsy;

import java.sql.SQLException;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.oskari.helpers.BundleHelper;

import fi.nls.oskari.domain.map.view.Bundle;

public class V1_00_6__register_content_editor_bundle extends BaseJavaMigration {

    private static final String CONTENT_EDITOR = "content-editor";

    public void migrate(Context context) throws SQLException {
        Bundle contenEditorTool = new Bundle();
        contenEditorTool.setConfig("{}");
        contenEditorTool.setState("{}");
        contenEditorTool.setName(CONTENT_EDITOR);
        BundleHelper.registerBundle(context.getConnection(), contenEditorTool);
    }
}
