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
import hsy.seutumaisa.domain.LandmassHelper;
import hsy.seutumaisa.domain.LandmassMunicipality;
import hsy.seutumaisa.domain.LandmassProject;
import hsy.seutumaisa.service.LandmassProjectService;

@OskariActionRoute("LandmassProject")
public class LandmassProjectHandler extends SeutumaisaRestActionHandler {

    private static final String PARAM_ID = "id";

    private LandmassProjectService service;

    @Override
    public void init() {
        super.init();
        service = OskariComponentManager.getComponentOfType(LandmassProjectService.class);
    }

    @Override
    public void preProcess(ActionParameters params) throws ActionException {
        super.preProcess(params);
        params.requireLoggedInUser();
    }

    @Override
    public void handleGet(ActionParameters params) throws ActionException {
        List<LandmassProject> projects = service.getAll().stream()
                .filter(x -> canRead(params.getUser(), x))
                .collect(Collectors.toList());
        writeResponse(params, projects);
    }

    @Override
    public void handlePost(ActionParameters params) throws ActionException {
        LandmassProject project = deserialize(params.getPayLoad());
        if (!canWrite(params.getUser(), project)) {
            throw new ActionDeniedException("No permission to create project");
        }
        service.save(project);
        writeResponse(params, project);
    }

    @Override
    public void handlePut(ActionParameters params) throws ActionException {
        LandmassProject project = deserialize(params.getPayLoad());
        if (project.getId() == null || project.getId() <= 0L) {
            throw new ActionParamsException("Update requires valid project id");
        }
        if (!canWrite(params.getUser(), project)) {
            throw new ActionDeniedException("No permission to update project");
        }

        int id = project.getId();
        LandmassProject db = service.getById(id);
        if (db == null) {
            ResponseHelper.writeError(params, "Could not find the project to update", 404);
            return;
        }
        if (!canWrite(params.getUser(), db)) {
            throw new ActionDeniedException("No permission to update project");
        }

        service.update(project);
        writeResponse(params, project);
    }

    @Override
    public void handleDelete(ActionParameters params) throws ActionException {
        int id = params.getRequiredParamInt(PARAM_ID);

        LandmassProject db = service.getById(id);
        if (db == null) {
            ResponseHelper.writeError(params, "Could not find project", 404);
            return;
        }
        if (!canWrite(params.getUser(), db)) {
            throw new ActionDeniedException("No permission to delete project");
        }

        service.delete(id);
        JSONObject response = JSONHelper.createJSONObject(PARAM_ID, id);
        ResponseHelper.writeResponse(params, response);
    }

    private static LandmassProject deserialize(String json) throws ActionParamsException {
        try {
            return OM.readValue(json, LandmassProject.class);
        } catch (Exception ex) {
            throw new ActionParamsException("Coudn't parse LandmassProject from: " + json, ex);
        }
    }

    private static boolean canRead(User user, LandmassProject project) {
        return user.hasRole(LandmassHelper.getRoleNameSeutumassaAdmin()) ||
                LandmassMunicipality.byId(project.getKunta())
                .map(m -> new String[] { m.getRoleName(), m.getAdminRoleName() })
                .map(roleNames -> user.hasAnyRoleIn(roleNames))
                .orElse(false);
    }

    private static boolean canWrite(User user, LandmassProject project) {
        return user.hasRole(LandmassHelper.getRoleNameSeutumassaAdmin()) ||
                LandmassMunicipality.byId(project.getKunta())
                    .map(m -> m.getAdminRoleName())
                    .map(r -> user.hasRole(r))
                    .orElse(false);
    }

}
