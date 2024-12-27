package hsy.seutumaisa.actions;

import java.util.List;

import org.json.JSONObject;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import fi.nls.oskari.annotation.OskariActionRoute;
import fi.nls.oskari.control.ActionException;
import fi.nls.oskari.control.ActionParameters;
import fi.nls.oskari.control.ActionParamsException;
import fi.nls.oskari.service.OskariComponentManager;
import fi.nls.oskari.util.JSONHelper;
import fi.nls.oskari.util.ResponseHelper;
import hsy.seutumaisa.domain.LandMassArea;
import hsy.seutumaisa.service.LandMassAreaService;

@OskariActionRoute("LandMassArea")
public class LandMassAreaHandler extends SeutumaisaRestActionHandler {

    private static final String PARAM_ID = "id";
    private static final String PARAM_LON = "lon";
    private static final String PARAM_LAT = "lat";

    private static final ObjectMapper OM = new ObjectMapper();
    static {
        OM.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        OM.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OM.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    private LandMassAreaService service;

    @Override
    public void init() {
        super.init();
        service = OskariComponentManager.getComponentOfType(LandMassAreaService.class);
    }

    @Override
    public void handleGet(ActionParameters params) throws ActionException {
        double lon = params.getRequiredParamDouble(PARAM_LON);
        double lat = params.getRequiredParamDouble(PARAM_LAT);

        List<LandMassArea> areas = service.getByCoordinate(lon, lat);

        try {
            byte[] b = OM.writeValueAsBytes(areas);
            ResponseHelper.writeResponse(params, 200, ResponseHelper.CONTENT_TYPE_JSON_UTF8, b);
        } catch (JsonProcessingException e) {
            throw new ActionException("Error occured when serializing to JSON", e);
        }
    }

    @Override
    public void handlePost(ActionParameters params) throws ActionException {
        params.requireLoggedInUser();

        LandMassArea area = deserialize(params.getPayLoad());
        long id = service.save(area);
        area.setId(id);
        JSONObject response = JSONHelper.createJSONObject(PARAM_ID, id);
        ResponseHelper.writeResponse(params, response);
    }

    @Override
    public void handlePut(ActionParameters params) throws ActionException {
        params.requireLoggedInUser();

        LandMassArea area = deserialize(params.getPayLoad());
        service.update(area);
        JSONObject response = JSONHelper.createJSONObject(PARAM_ID, area.getId());
        ResponseHelper.writeResponse(params, response);
    }

    @Override
    public void handleDelete(ActionParameters params) throws ActionException {
        params.requireLoggedInUser();

        long id = params.getRequiredParamLong(PARAM_ID);
        service.delete(id);
        JSONObject response = JSONHelper.createJSONObject(PARAM_ID, id);
        ResponseHelper.writeResponse(params, response);
    }

    private LandMassArea deserialize(String json) throws ActionParamsException {
        try {
            return OM.readValue(json, LandMassArea.class);
        } catch (Exception ex) {
            throw new ActionParamsException("Coudn't parse LandMassArea from: " + json, ex);
        }
    }

}
