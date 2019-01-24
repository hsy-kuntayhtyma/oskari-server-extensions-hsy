package flyway.dev;

import fi.nls.oskari.db.BundleHelper;
import fi.nls.oskari.domain.User;
import fi.nls.oskari.domain.map.view.Bundle;
import fi.nls.oskari.log.LogFactory;
import fi.nls.oskari.log.Logger;
import fi.nls.oskari.service.ServiceException;
import fi.nls.oskari.service.UserService;
import fi.nls.oskari.user.DatabaseUserService;
import fi.nls.oskari.user.MybatisRoleService;
import org.flywaydb.core.api.migration.jdbc.JdbcMigration;

import java.sql.Connection;

public class V1_00_0__add_default_admin_user implements JdbcMigration {

    private Logger log = LogFactory.getLogger(V1_00_0__add_default_admin_user.class);

    public void migrate(Connection connection) {

        UserService dbService = null;
        try {
            dbService = DatabaseUserService.getInstance();
        } catch (ServiceException se) {
            log.error(se, "Unable to initialize User service!");
        }
        try {
            dbService.updateUserPassword("admin", "oskari");
        } catch (ServiceException se) {
            log.error(se, "Cannot update password");
        }
    }
}
