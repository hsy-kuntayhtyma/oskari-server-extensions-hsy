package flyway.seutumaisa;

import fi.nls.oskari.db.ViewHelper;
import fi.nls.oskari.domain.Role;
import fi.nls.oskari.domain.map.view.View;
import fi.nls.oskari.log.LogFactory;
import fi.nls.oskari.log.Logger;
import fi.nls.oskari.map.view.ViewService;
import fi.nls.oskari.map.view.AppSetupServiceMybatisImpl;
import fi.nls.oskari.user.MybatisRoleService;
import fi.nls.oskari.util.PropertyUtil;
import org.flywaydb.core.api.migration.jdbc.JdbcMigration;
import org.json.JSONObject;
import java.sql.Connection;

public class V1_03_0__add_seutumaisa_view implements JdbcMigration {
    private static final ViewService VIEW_SERVICE = new AppSetupServiceMybatisImpl();
    private static final Logger LOG = LogFactory.getLogger(V1_03_0__add_seutumaisa_view.class);
    private static final String ROLE_SEUTUMAISA = "SeutuMaisa";


    public void migrate(Connection connection) throws Exception {
        final String file = PropertyUtil.get("flyway.hsy.putki.1_03_0.file", "seutumaisa-view.json");

        // configure the view that should be used as default options
        final int defaultViewId = PropertyUtil.getOptional("flyway.hsy.1_03_0.view", (int) VIEW_SERVICE.getDefaultViewId());
        try {
            // Add SeutuMaisa role
            MybatisRoleService roles = new MybatisRoleService();
            Role role = new Role();
            role.setName(ROLE_SEUTUMAISA);
            long roleId = roles.insert(role);
            LOG.info("SeutuMaisa role added with id", roleId);

            // load view from json and update startups for bundles
            JSONObject json = ViewHelper.readViewFile(file);
            View view = ViewHelper.createView(json);

            // save to db
            VIEW_SERVICE.addView(view);
            LOG.info("SeutuMaisa view added with uuid", view.getUuid(), ", and view id", view.getId());


            LOG.info("Add oskari-ext.properties -file followings:\n",
                    "\tview.default.SeutuMaisa="+view.getId()+"\n" +
                    "\tview.default.roles=Admin, User, Guest, SeutuMaisa\n",
                    "\tactionhandler.GetAppSetup.dynamic.bundles = content-editor\n",
                    "\tactionhandler.GetAppSetup.dynamic.bundle.content-editor.roles = SeutuMaisa");
            LOG.info("Also remember add new users to SeutuMaisa role");
        } catch (Exception e) {
            LOG.warn(e, "Something went wrong while inserting the view!",
                    "The update failed so to have an SeutuMaisa view you need to remove this update from the database table oskari_status_hsy, " +
                            "tune the template file:", file, " and restart the server to try again");
            throw e;
        }
    }
}