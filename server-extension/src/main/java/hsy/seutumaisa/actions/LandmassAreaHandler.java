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
import hsy.seutumaisa.domain.LandmassArea;
import hsy.seutumaisa.service.LandmassAreaService;

@OskariActionRoute("LandmassArea")
public class LandmassAreaHandler extends SeutumaisaRestActionHandler {

    private static final String PARAM_ID = "id";
    private static final String PARAM_LON = "lon";
    private static final String PARAM_LAT = "lat";

    private static final ObjectMapper OM = new ObjectMapper();
    static {
        OM.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        OM.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        OM.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OM.setSerializationInclusion(JsonInclude.Include.ALWAYS);
    }

    private LandmassAreaService service;

    @Override
    public void init() {
        super.init();
        service = OskariComponentManager.getComponentOfType(LandmassAreaService.class);
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

        List<LandmassArea> areas = service.getAreasByCoordinate(lon, lat).stream()
                .filter(x -> canRead(params.getUser(), x))
                .toList();

        writeResponse(params, areas);
    }

    @Override
    public void handlePost(ActionParameters params) throws ActionException {
        LandmassArea area = deserialize(params.getPayLoad());

        if (!canWrite(params.getUser(), area)) {
            throw new ActionDeniedException("No permission to write area");
        }

        service.save(area);

        writeResponse(params, area);
    }

    @Override
    public void handlePut(ActionParameters params) throws ActionException {
        LandmassArea area = deserialize(params.getPayLoad());

        if (area.getId() == null || area.getId() <= 0L) {
            throw new ActionParamsException("Update requires valid area id");
        }
        int id = area.getId();

        if (!canWrite(params.getUser(), area)) {
            throw new ActionDeniedException("No permission to overwrite area");
        }

        LandmassArea dbArea = service.getAreaById(id);
        if (dbArea == null) {
            ResponseHelper.writeError(params, "Could not find any area", 404);
            return;
        }
        if (!canEdit(params.getUser(), dbArea)) {
            throw new ActionDeniedException("No permission to overwrite area");
        }

        service.update(area);
        writeResponse(params, area);
    }

    @Override
    public void handleDelete(ActionParameters params) throws ActionException {
        int id = params.getRequiredParamInt(PARAM_ID);

        LandmassArea dbArea = service.getAreaById(id);
        if (dbArea == null) {
            ResponseHelper.writeError(params, "Could not find any area", 404);
            return;
        }

        if (!canWrite(params.getUser(), dbArea)) {
            throw new ActionDeniedException("No permission to delete area");
        }

        service.delete(id);
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

    private static <T> void writeResponse(ActionParameters params, T response) throws ActionException {
        try {
            byte[] b = OM.writeValueAsBytes(response);
            ResponseHelper.writeResponse(params, 200, ResponseHelper.CONTENT_TYPE_JSON_UTF8, b);
        } catch (JsonProcessingException e) {
            throw new ActionException("Error occured when serializing to JSON", e);
        }
    }

    private static boolean canRead(User user, LandmassArea area) {
        return true;
    }

    private static boolean canWrite(User user, LandmassArea area) {
        return true;
    }

    private static boolean canEdit(User user, LandmassArea area) {
        return true;
    }

}
