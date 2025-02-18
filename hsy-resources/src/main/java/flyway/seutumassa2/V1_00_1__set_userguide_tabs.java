package flyway.seutumassa2;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.json.JSONArray;
import org.json.JSONObject;
import org.oskari.helpers.AppSetupHelper;
import org.oskari.helpers.BundleHelper;

import fi.nls.oskari.domain.map.view.Bundle;
import fi.nls.oskari.log.LogFactory;
import fi.nls.oskari.log.Logger;
import fi.nls.oskari.util.JSONHelper;

public class V1_00_1__set_userguide_tabs extends BaseJavaMigration {

    private static final Logger LOG = LogFactory.getLogger(V1_00_1__set_userguide_tabs.class);

    private static final String USERGUIDE = "userguide";

    public void migrate(Context context) throws Exception {
        Connection c = context.getConnection();
        if (!BundleHelper.isBundleRegistered(c, USERGUIDE)) {
            LOG.warn("Bundle not registered!", USERGUIDE);
            return;
        }

        JSONArray tabs = new JSONArray(Arrays.asList(
                toTab("ohje_yleista", "Yleistä"),
                toTab("ohje_karttaikkuna", "Karttaikkuna"),
                toTab("ohje_tyokalut", "Työkalut"),
                toTab("ohje_maamassahaku", "Maamassahaku"),
                toTab("ohje_haku", "Haku"),
                toTab("ohje_karttatasot", "Karttatasot")
        ));

        for (long appsetupId : V1_00_0__add_seutumaisa_search.getSeutumaisaAppsetupIds(c)) {
            setUserguideTabs(c, appsetupId, tabs);
        }
    }

    private static JSONObject toTab(String tags, String title) {
        JSONObject obj = new JSONObject();
        JSONHelper.putValue(obj, "tags", tags);
        JSONHelper.putValue(obj, "title", title);
        return obj;
    }

    private static void setUserguideTabs(Connection c, long appsetupId, JSONArray tabs) throws SQLException {
        Bundle b = AppSetupHelper.getAppBundle(c, appsetupId, USERGUIDE);
        JSONObject config = b.getConfigJSON();
        JSONHelper.putValue(config, "tabs", tabs);
        AppSetupHelper.updateAppBundle(c, appsetupId, b);
    }

}
