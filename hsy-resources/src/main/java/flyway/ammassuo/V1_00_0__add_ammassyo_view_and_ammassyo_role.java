package flyway.ammassuo;

import fi.nls.oskari.db.ViewHelper;
import fi.nls.oskari.domain.Role;
import fi.nls.oskari.domain.map.view.View;
import fi.nls.oskari.log.LogFactory;
import fi.nls.oskari.log.Logger;
import fi.nls.oskari.map.view.ViewService;
import fi.nls.oskari.map.view.ViewServiceIbatisImpl;
import fi.nls.oskari.user.MybatisRoleService;
import fi.nls.oskari.util.PropertyUtil;
import org.flywaydb.core.api.migration.jdbc.JdbcMigration;
import org.json.JSONObject;

import java.sql.Connection;

public class V1_00_0__add_ammassyo_view_and_ammassyo_role implements JdbcMigration {
    private static final ViewService VIEW_SERVICE = new ViewServiceIbatisImpl();
    private static final Logger LOG = LogFactory.getLogger(V1_00_0__add_ammassyo_view_and_ammassyo_role.class);
    private static final String ROLE_AMMASSUO = "Ammassuo";


    public void migrate(Connection connection) throws Exception {
        final String file = PropertyUtil.get("flyway.ammassuo.1_01_0.file", "hsy-ammassuo-view.json");

        // configure the view that should be used as default options
        final int defaultViewId = PropertyUtil.getOptional("flyway.ammassuo.1_01_0.view", (int) VIEW_SERVICE.getDefaultViewId());
        try {
            // Add ämmässuo role
            MybatisRoleService roles = new MybatisRoleService();
            Role ammassuoRole = roles.findRoleByName(ROLE_AMMASSUO);
            if(ammassuoRole == null) {
                Role role = new Role();
                role.setName(ROLE_AMMASSUO);
                long roleId = roles.insert(role);
                LOG.info("Ammassuo role added with id", roleId);
            }

            View ammassuoView = VIEW_SERVICE.getViewWithConf("Ämmässuo näkymä");
            if(ammassuoView == null) {
                // load view from json and update startups for bundles
                JSONObject json = ViewHelper.readViewFile(file);
                View view = ViewHelper.createView(json);

                // save to db
                VIEW_SERVICE.addView(view);
                LOG.info("Ammassuo view added with uuid", view.getUuid(), ", and view id", view.getId());


                LOG.info("Add oskari-ext.properties -file followings:\n",
                        "\tview.default.Ammassuo=" + view.getId() + "\n" +
                                "\tview.default.roles=Admin, User, Guest, Ammassuo\n",
                        "\tactionhandler.GetAppSetup.dynamic.bundles = content-editor\n",
                        "\tactionhandler.GetAppSetup.dynamic.bundle.content-editor.roles = Ammassuo");
                LOG.info("Also remember add new users to Ammassuo role");
            }
        } catch (Exception e) {
            LOG.warn(e, "Something went wrong while inserting the view!",
                    "The update failed so to have an amamssuo view you need to remove this update from the database table oskari_status_ammassuo, " +
                            "tune the template file:", file, " and restart the server to try again");
            throw e;
        }
    }
}
