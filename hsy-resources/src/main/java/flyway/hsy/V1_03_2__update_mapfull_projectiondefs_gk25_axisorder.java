package flyway.hsy;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.json.JSONException;
import org.json.JSONObject;
import org.oskari.helpers.AppSetupHelper;

import fi.nls.oskari.domain.map.view.Bundle;
import fi.nls.oskari.log.LogFactory;
import fi.nls.oskari.log.Logger;

public class V1_03_2__update_mapfull_projectiondefs_gk25_axisorder extends BaseJavaMigration {

    private static final Logger LOG = LogFactory.getLogger(V1_03_2__update_mapfull_projectiondefs_gk25_axisorder.class);

    public void migrate(Context context) throws Exception {
        Connection c = context.getConnection();
        List<Long> allAppsetupIds = AppSetupHelper.getSetupsForType(c);
        for (long appsetupId : allAppsetupIds) {
            setGK25ProjDefAxisOrder(c, appsetupId);
        }
    }

    private void setGK25ProjDefAxisOrder(Connection c, long appsetupId)
            throws SQLException, JSONException {
        Bundle mapfull = AppSetupHelper.getAppBundle(c, appsetupId, "mapfull");
        if (mapfull == null) {
            LOG.debug("Skipping appsetup " + appsetupId
                    + " no mapfull bundle");
            return;
        }

        JSONObject config = mapfull.getConfigJSON();
        if (config == null) {
            LOG.debug("Skipping appsetup " + appsetupId
                    + " mapfull config is null");
            return;
        }

        JSONObject projectionDefs = config.optJSONObject("projectionDefs");
        if (projectionDefs == null) {
            LOG.debug("Skipping appsetup " + appsetupId
                    + " mapfull config does not contain value for key 'projectionDefs'");
            return;
        }

        String projDef = projectionDefs.optString("EPSG:3879");
        if (projDef == null) {
            LOG.debug("Skipping appsetup " + appsetupId
                    + " mapfull config projectionDefs does not contain value for key 'EPSG:3879'");
            return;
        }

        if (projDef.contains("+axis=neu")) {
            LOG.debug("Skipping appsetup " + appsetupId
                    + " +axis=neu already present in EPSG:3897 projDef");
            return;
        }

        projectionDefs.put("EPSG:3879", projDef + " +axis=neu");
        AppSetupHelper.updateAppBundle(c, appsetupId, mapfull);
        LOG.info("Updated " + appsetupId + " mapfull config to: " + mapfull.getConfig());
    }

}
