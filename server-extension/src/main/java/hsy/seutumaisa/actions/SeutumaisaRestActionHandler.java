package hsy.seutumaisa.actions;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import fi.nls.oskari.control.ActionDeniedException;
import fi.nls.oskari.control.ActionException;
import fi.nls.oskari.control.ActionParameters;
import fi.nls.oskari.control.RestActionHandler;
import fi.nls.oskari.util.PropertyUtil;
import fi.nls.oskari.util.ResponseHelper;

public class SeutumaisaRestActionHandler extends RestActionHandler {

    private final static String PROPERTY_ENABLED = "seutumassa.enabled";

    protected static final ObjectMapper OM = new ObjectMapper();
    static {
        OM.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        OM.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        OM.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OM.setSerializationInclusion(JsonInclude.Include.ALWAYS);
    }

    public void preProcess(ActionParameters params) throws ActionException {
        if (!PropertyUtil.getOptional(PROPERTY_ENABLED, false)) {
            throw new ActionDeniedException("Not seutumaisa configured");
        }
    }

    protected static <T> void writeResponse(ActionParameters params, T response) throws ActionException {
        try {
            byte[] b = OM.writeValueAsBytes(response);
            ResponseHelper.writeResponse(params, 200, ResponseHelper.CONTENT_TYPE_JSON_UTF8, b);
        } catch (JsonProcessingException e) {
            throw new ActionException("Error occured when serializing to JSON", e);
        }
    }

}
