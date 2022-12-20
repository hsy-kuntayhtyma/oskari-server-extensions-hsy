package flyway.hsy;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.oskari.helpers.AppSetupHelper;
import org.oskari.helpers.BundleHelper;

import fi.nls.oskari.domain.map.view.Bundle;

public class V1_03_0__change_hierarchical_layerlist_to_layerlist extends BaseJavaMigration {

    public void migrate(Context context) throws Exception {
        String bundleToAdd = "layerlist";
        String bundleToRemove = "hierarchical-layerlist";

        Connection c = context.getConnection();

        List<Long> appsetupIds = AppSetupHelper.getSetupsForUserAndDefaultType(c);

        BundleHelper.registerBundle(c, bundleToAdd);
        for (long appsetupId : appsetupIds) {
            swapBundle(c, appsetupId, bundleToAdd, bundleToRemove);
        }
        BundleHelper.unregisterBundle(c, bundleToRemove);
    }
    
    private void swapBundle(Connection c, long appsetupId, String bundleToAdd, String bundleToRemove) throws SQLException {
        Bundle bundle = AppSetupHelper.getAppBundle(c, appsetupId, bundleToRemove);
        if (bundle == null) {
            return;
        }

        final int seqno = bundle.getSeqNo();
        AppSetupHelper.removeBundleFromApp(c, appsetupId, bundleToRemove);

        AppSetupHelper.addBundleToApp(c, appsetupId, bundleToAdd);
        Bundle added = AppSetupHelper.getAppBundle(c, appsetupId, bundleToAdd);
        added.setSeqNo(seqno);
        AppSetupHelper.updateAppBundle(c, appsetupId, added);
    }

}
