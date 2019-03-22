package flyway.hsy;

import fi.nls.oskari.log.LogFactory;
import fi.nls.oskari.log.Logger;
import fi.nls.oskari.util.PropertyUtil;
import helpers.LayerHelper;
import org.flywaydb.core.api.migration.jdbc.JdbcMigration;

import java.sql.Connection;

public class V1_02_2__update_background_selection_plugins_layers implements JdbcMigration {
    private static final Logger LOG = LogFactory.getLogger(V1_02_2__update_background_selection_plugins_layers.class);

    public void migrate(Connection connection) throws Exception {
        String[] layers = PropertyUtil.getCommaSeparatedList("flyway.hsy.V1_02_2.layers");
        boolean skip = PropertyUtil.getOptional("flyway.hsy.V1_02_2.skip", false);
        if (!skip) {
            LayerHelper.setBackgroundSelectionPluginLayers(connection, layers);
        }
    }
}
