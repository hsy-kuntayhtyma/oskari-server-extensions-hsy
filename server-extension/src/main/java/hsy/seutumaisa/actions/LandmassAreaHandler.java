package hsy.seutumaisa.actions;

import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONObject;

import fi.nls.oskari.annotation.OskariActionRoute;
import fi.nls.oskari.control.ActionDeniedException;
import fi.nls.oskari.control.ActionException;
import fi.nls.oskari.control.ActionParameters;
import fi.nls.oskari.control.ActionParamsException;
import fi.nls.oskari.domain.User;
import fi.nls.oskari.service.OskariComponentManager;
import fi.nls.oskari.util.JSONHelper;
import fi.nls.oskari.util.ResponseHelper;
import hsy.seutumaisa.domain.LandmassArea;
import hsy.seutumaisa.domain.LandmassHelper;
import hsy.seutumaisa.domain.LandmassMunicipality;
import hsy.seutumaisa.domain.LandmassProject;
import hsy.seutumaisa.service.LandmassAreaService;
import hsy.seutumaisa.service.LandmassProjectService;

@OskariActionRoute("LandmassArea")
public class LandmassAreaHandler extends SeutumaisaRestActionHandler {

    private static final String PARAM_ID = "id";
    private static final String PARAM_LON = "lon";
    private static final String PARAM_LAT = "lat";

    private LandmassAreaService areaService;
    private LandmassProjectService projectService;

    @Override
    public void init() {
        super.init();
        areaService = OskariComponentManager.getComponentOfType(LandmassAreaService.class);
        projectService = OskariComponentManager.getComponentOfType(LandmassProjectService.class);
    }

    @Override
    public void preProcess(ActionParameters params) throws ActionException {
        super.preProcess(params);
        params.requireLoggedInUser();
    }

    @Override
    public void handleGet(ActionParameters params) throws ActionException {
        double lon = params.getRequiredParamDouble(PARAM_LON);
        double lat = params.getRequiredParamDouble(PARAM_LAT);

        List<LandmassArea> areas = areaService.getAreasByCoordinate(lon, lat).stream()
                .filter(x -> canRead(params.getUser(), x))
                .collect(Collectors.toList());

        writeResponse(params, areas);
    }

    @Override
    public void handlePost(ActionParameters params) throws ActionException {
        LandmassArea area = deserialize(params.getPayLoad());

        if (!canCreate(params.getUser(), area)) {
            throw new ActionDeniedException("No permission to create area");
        }
        area.setCreatedByUserId((int) params.getUser().getId());

        areaService.save(area);

        writeResponse(params, area);
    }

    @Override
    public void handlePut(ActionParameters params) throws ActionException {
        LandmassArea area = deserialize(params.getPayLoad());

        if (area.getId() == null || area.getId() <= 0L) {
            throw new ActionParamsException("Update requires valid area id");
        }

        if (!canCreate(params.getUser(), area)) {
            throw new ActionDeniedException("No permission to update area");
        }

        int id = area.getId();
        LandmassArea dbArea = areaService.getAreaById(id);
        if (dbArea == null) {
            ResponseHelper.writeError(params, "Could not find any area", 404);
            return;
        }
        if (!canEdit(params.getUser(), dbArea)) {
            throw new ActionDeniedException("No permission to update area");
        }

        areaService.update(area);
        writeResponse(params, area);
    }

    @Override
    public void handleDelete(ActionParameters params) throws ActionException {
        int id = params.getRequiredParamInt(PARAM_ID);

        LandmassArea dbArea = areaService.getAreaById(id);
        if (dbArea == null) {
            ResponseHelper.writeError(params, "Could not find any area", 404);
            return;
        }

        if (!canEdit(params.getUser(), dbArea)) {
            throw new ActionDeniedException("No permission to delete area");
        }

        areaService.delete(id);
        JSONObject response = JSONHelper.createJSONObject(PARAM_ID, id);
        ResponseHelper.writeResponse(params, response);
    }

    private static LandmassArea deserialize(String json) throws ActionParamsException {
        try {
            return OM.readValue(json, LandmassArea.class);
        } catch (Exception ex) {
            throw new ActionParamsException("Coudn't parse LandMassArea from: " + json, ex);
        }
    }

    private static boolean canRead(User user, LandmassArea area) {
        return LandmassMunicipality.byId(area.getKunta())
                .map(m -> new String[] { LandmassHelper.getRoleNameSeutumassaAdmin(), m.getRoleName(), m.getAdminRoleName() })
                .map(roleNames -> user.hasAnyRoleIn(roleNames))
                .orElse(false);
    }

    private boolean canCreate(User user, LandmassArea area) {
        if (LandmassMunicipality.byId(area.getKunta()).isEmpty()) {
            return false;
        }

        LandmassMunicipality m = LandmassMunicipality.byId(area.getKunta()).get();
        if (!user.hasAnyRoleIn(new String[] { LandmassHelper.getRoleNameSeutumassaAdmin(), m.getRoleName(), m.getAdminRoleName() })) {
            return false;
        }

        if (user.hasRole(LandmassHelper.getRoleNameSeutumassaAdmin()) || user.hasRole(m.getAdminRoleName()) || area.getHankealue_id() == null) {
            return true;
        }

        // Verify user is either editor or manager in hankealue
        LandmassProject p = projectService.getById(area.getHankealue_id());
        if (p == null) {
            return false;
        }
        int userId = (int) user.getId();
        return p.isInEditors(userId) || p.isInManagers(userId);
    }

    private boolean canEdit(User user, LandmassArea area) {
        if (LandmassMunicipality.byId(area.getKunta()).isEmpty()) {
            return false;
        }

        LandmassMunicipality m = LandmassMunicipality.byId(area.getKunta()).get();
        if (!user.hasAnyRoleIn(new String[] { LandmassHelper.getRoleNameSeutumassaAdmin(), m.getRoleName(), m.getAdminRoleName() })) {
            return false;
        }

        if (user.hasRole(LandmassHelper.getRoleNameSeutumassaAdmin()) || user.hasRole(m.getAdminRoleName()) || area.getHankealue_id() != null) {
            return true;
        }

        // Verify user is either manager in hankealue
        LandmassProject p = projectService.getById(area.getHankealue_id());
        if (p == null) {
            return false;
        }

        int userId = (int) user.getId();
        if (p.isInManagers(userId)) {
            return true;
        }
        // or an editor (and editing area they created)
        if (p.isInEditors(userId) && area.getCreatedByUserId() != null && area.getCreatedByUserId() == userId) {
            return true;
        }

        return false;
    }

}
