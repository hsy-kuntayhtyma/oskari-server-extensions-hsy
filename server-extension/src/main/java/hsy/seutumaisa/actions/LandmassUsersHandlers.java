package hsy.seutumaisa.actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import fi.nls.oskari.util.ResponseHelper;

@OskariActionRoute("LandmassUsers")
public class LandmassUsersHandlers extends SeutumaisaRestActionHandler {

    private static Logger LOG = LogFactory.getLogger(LandmassUsersHandlers.class);

    private static final String ROLE_SEUTUMAISA_PREFIX = "SeutuMaisa_";
    private static final String ROLE_ADMIN_SUFFIX = "_Admin";
    private static final List<String> MUNICIPALITIES = List.of("Espoo", "Helsinki", "Vantaa");
    private static final List<String> MUNICIPALITY_CODES = List.of("049", "091", "092");

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

        List<String> municipalities = getMunicipalitiesUserIsWorkspaceAdminIn(params.getUser());
        if (municipalities.isEmpty()) {
            throw new ActionDeniedException("User is not admin or workspace admin");
        }

        // Fetch these only once
        Role[] allRoles = getAllRoles(service);
        
        Set<User> users = new LinkedHashSet<>();
        Map<Long, List<String>> municipalitiesByUserId = new HashMap<>();
        for (int i = 0; i < MUNICIPALITIES.size(); i++) {
            Role role = findRoleByMunicipality(MUNICIPALITIES.get(i), allRoles);
            if (role == null) {
                continue;
            }
            for (User user : getUsersByRole(service, role.getId())) {
                municipalitiesByUserId.computeIfAbsent(user.getId(), __ -> new ArrayList<>()).add(MUNICIPALITY_CODES.get(i));
                users.add(user);
            }
        }
            
        JSONArray toReturn = new JSONArray();
        for (User user : users) {
            toReturn.put(toJson(user, municipalitiesByUserId.get(user.getId())));
        }
        
        ResponseHelper.writeResponse(params, toReturn);
    }

    private static List<String> getMunicipalitiesUserIsWorkspaceAdminIn(User user) {
        if (user.isAdmin()) {
            return MUNICIPALITIES;
        }
        return MUNICIPALITIES.stream()
                .filter(m -> user.getRoles().stream().anyMatch(r -> r.getName().equals(getAdminRoleName(m))))
                .toList();
    }

    private static String getAdminRoleName(String municipality) {
        return getRoleName(municipality) + ROLE_ADMIN_SUFFIX;
    }

    private static String getRoleName(String municipality) {
        return ROLE_SEUTUMAISA_PREFIX + municipality;
    }

    private static Role[] getAllRoles(UserService service) {
        try {
            return service.getRoles();
        } catch (ServiceException e) {
            throw new ServiceRuntimeException("Failed to get roles", e);
        }
    }

    private static Role findRoleByMunicipality(String municipality, Role[] roles) {
        return Arrays.stream(roles)
                .filter(x -> x.getName().equals(getRoleName(municipality)))
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

    private static JSONObject toJson(User user, List<String> municipalities) {
        try {
            JSONObject u = new JSONObject();
            u.put("id", user.getId());
            u.put("nickName", user.getScreenname());
            u.put("municipalities", municipalities);
            return u;
        } catch (Exception ignore) {
            // wont happen
            return null;
        }
    }

}
