package flyway.hsy;

import fi.nls.oskari.util.PropertyUtil;
import helpers.LayerHelper;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

public class V1_02_2__update_background_selection_plugins_layers extends BaseJavaMigration {

    public void migrate(Context context) throws Exception {
        String[] layers = PropertyUtil.getCommaSeparatedList("flyway.hsy.V1_02_2.layers");
        boolean skip = PropertyUtil.getOptional("flyway.hsy.V1_02_2.skip", false);
        if (!skip) {
            LayerHelper.setBackgroundSelectionPluginLayers(context.getConnection(), layers);
        }
    }
}
