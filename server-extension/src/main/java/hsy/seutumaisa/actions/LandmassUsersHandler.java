package hsy.seutumaisa.actions;

import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import fi.nls.oskari.annotation.OskariActionRoute;
import fi.nls.oskari.control.ActionDeniedException;
import fi.nls.oskari.control.ActionException;
import fi.nls.oskari.control.ActionParameters;
import fi.nls.oskari.domain.Role;
import fi.nls.oskari.domain.User;
import fi.nls.oskari.log.LogFactory;
import fi.nls.oskari.log.Logger;
import fi.nls.oskari.service.ServiceException;
import fi.nls.oskari.service.ServiceRuntimeException;
import fi.nls.oskari.service.UserService;
import fi.nls.oskari.util.JSONHelper;
import fi.nls.oskari.util.ResponseHelper;
import hsy.seutumaisa.domain.LandmassMunicipality;

@OskariActionRoute("LandmassUsers")
public class LandmassUsersHandler extends SeutumaisaRestActionHandler {

    private static Logger LOG = LogFactory.getLogger(LandmassUsersHandler.class);

    private UserService service;

    @Override
    public void init() {
        super.init();
        try {
            service = UserService.getInstance();
        } catch (Exception e) {
            LOG.error(e, "Failed to get UserService");
        }
    }

    @Override
    public void handleGet(ActionParameters params) throws ActionException {
        params.requireLoggedInUser();

        LandmassMunicipality[] municipalities = Arrays.stream(LandmassMunicipality.values())
                .filter(m -> params.getUser().hasRole(m.getAdminRoleName()))
                .toArray(n -> new LandmassMunicipality[n]);
        if (municipalities.length == 0) {
            throw new ActionDeniedException("User is not admin or workspace admin");
        }

        // Fetch these only once
        Role[] allRoles = getAllRoles(service);

        JSONObject toReturn = new JSONObject();
        for (LandmassMunicipality m : municipalities) {
            Role r = findRoleByMunicipality(m, allRoles);
            if (r == null) {
                continue;
            }
            JSONArray users = new JSONArray();
            getUsersByRole(service, r.getId()).stream()
                .map(LandmassUsersHandler::toJson)
                .forEach(users::put);
            JSONHelper.put(toReturn, m.getId(), users);
        }

        ResponseHelper.writeResponse(params, toReturn);
    }

    private static Role[] getAllRoles(UserService service) {
        try {
            return service.getRoles();
        } catch (ServiceException e) {
            throw new ServiceRuntimeException("Failed to get roles", e);
        }
    }

    private static Role findRoleByMunicipality(LandmassMunicipality municipality, Role[] roles) {
        return Arrays.stream(roles)
                .filter(x -> x.getName().equals(municipality.getRoleName()))
                .findAny()
                .orElse(null);
    }

    private static List<User> getUsersByRole(UserService service, long roleId) {
        try {
            return service.getUsersByRole(roleId);
        } catch (ServiceException e) {
            throw new ServiceRuntimeException("Failed to get users by role", e);
        }
    }

    private static JSONObject toJson(User user) {
        try {
            JSONObject u = new JSONObject();
            u.put("id", user.getId());
            u.put("nickName", user.getScreenname());
            return u;
        } catch (Exception ignore) {
            // wont happen
            return null;
        }
    }

}
