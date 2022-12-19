package flyway.dev;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import fi.nls.oskari.log.LogFactory;
import fi.nls.oskari.log.Logger;
import fi.nls.oskari.service.ServiceException;
import fi.nls.oskari.service.UserService;
import fi.nls.oskari.user.DatabaseUserService;

public class V1_00_0__add_default_admin_user extends BaseJavaMigration {

    private Logger log = LogFactory.getLogger(V1_00_0__add_default_admin_user.class);

    public void migrate(Context context) {
        UserService dbService = null;
        try {
            dbService = DatabaseUserService.getInstance();
            try {
                dbService.updateUserPassword("admin", "oskari");
            } catch (ServiceException se) {
                log.error(se, "Cannot update password");
            }
        } catch (ServiceException se) {
            log.error(se, "Unable to initialize User service!");
        }
    }
}
