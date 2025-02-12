package hsy.seutumaisa.actions;

import java.util.List;

import org.json.JSONObject;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import fi.nls.oskari.annotation.OskariActionRoute;
import fi.nls.oskari.control.ActionDeniedException;
import fi.nls.oskari.control.ActionException;
import fi.nls.oskari.control.ActionParameters;
import fi.nls.oskari.control.ActionParamsException;
import fi.nls.oskari.domain.User;
import fi.nls.oskari.service.OskariComponentManager;
import fi.nls.oskari.util.JSONHelper;
import fi.nls.oskari.util.ResponseHelper;
import hsy.seutumaisa.domain.LandmassProject;
import hsy.seutumaisa.service.LandmassProjectService;

@OskariActionRoute("LandmassProject")
public class LandmassProjectHandler extends SeutumaisaRestActionHandler {

    private static final String PARAM_ID = "id";

    private static final ObjectMapper OM = new ObjectMapper();
    static {
        OM.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        OM.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OM.setSerializationInclusion(JsonInclude.Include.ALWAYS);
    }

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
        List<LandmassProject> projects = service.getAll();
        // Filter out the ones user has no permission to view
        writeResponse(params, projects);
    }

    @Override
    public void handlePost(ActionParameters params) throws ActionException {
        LandmassProject project = deserialize(params.getPayLoad());
        if (!canWrite(params.getUser(), project)) {
            throw new ActionDeniedException("No permission to write project");
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
        long id = project.getId();
        if (!canWrite(params.getUser(), project)) {
            throw new ActionDeniedException("No permission to write project");
        }
        LandmassProject db = service.getById(id);
        if (db == null) {
            ResponseHelper.writeError(params, "Could not find the project to update", 404);
            return;
        }
        if (!canWrite(params.getUser(), db)) {
            throw new ActionDeniedException("No permission to overwrite project");
        }

        service.update(project);
        writeResponse(params, project);
    }

    @Override
    public void handleDelete(ActionParameters params) throws ActionException {
        long id = params.getRequiredParamLong(PARAM_ID);

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

    private static <T> void writeResponse(ActionParameters params, T response) throws ActionException {
        try {
            byte[] b = OM.writeValueAsBytes(response);
            ResponseHelper.writeResponse(params, 200, ResponseHelper.CONTENT_TYPE_JSON_UTF8, b);
        } catch (JsonProcessingException e) {
            throw new ActionException("Error occured when serializing to JSON", e);
        }
    }

    private static boolean canWrite(User user, LandmassProject project) {
        return true;
    }

}
