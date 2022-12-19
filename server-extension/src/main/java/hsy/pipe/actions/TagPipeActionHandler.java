package hsy.pipe.actions;

import org.json.JSONObject;

import hsy.pipe.domain.TagPipeConfiguration;
import hsy.pipe.helpers.TagPipeHelper;
import fi.nls.oskari.annotation.OskariActionRoute;
import fi.nls.oskari.control.ActionException;
import fi.nls.oskari.control.ActionParameters;
import fi.nls.oskari.control.ActionParamsException;
import fi.nls.oskari.control.RestActionHandler;
import fi.nls.oskari.log.LogFactory;
import fi.nls.oskari.log.Logger;
import fi.nls.oskari.util.ResponseHelper;

@OskariActionRoute("SearchTagPipe")
public class TagPipeActionHandler extends RestActionHandler {

    private static final Logger log = LogFactory.getLogger(TagPipeActionHandler.class);

    protected final static String PARAM_TAG_ID = "tag_id";
    protected final static String PARAM_TAG_TYPE = "tag_type";
    protected final static String PARAM_TAG_ADDRESS = "tag_address";
    protected final static String PARAM_TAG_PIPE_SIZE = "tag_pipe_size";
    protected final static String PARAM_TAG_LOW_PRESSURE_LEVEL = "tag_low_pressure_level";
    protected final static String PARAM_TAG_MAX_PRESSURE_LEVEL = "tag_max_pressure_level";
    protected final static String PARAM_TAG_MAX_WATER_TAKE = "tag_max_water_take";
    protected final static String PARAM_TAG_MIN_PRESSURE_LEVEL = "tag_min_pressure_level";
    protected final static String PARAM_TAG_BOTTOM_HEIGHT = "tag_bottom_height";
    protected final static String PARAM_TAG_LOW_TAG_HEIGHT = "tag_low_tag_height";
    protected final static String PARAM_TAG_BARRAGE_HEIGHT = "tag_barrage_height";
    protected final static String PARAM_TAG_GROUND_HEIGHT = "tag_ground_height";
    protected final static String PARAM_TAG_OTHER_ISSUE = "tag_other_issue";
    protected final static String PARAM_TAG_GEOJSON = "tag_geojson";
    //Separate Finnish kiinteistötunnus (real estate ID) into 4 different fields
    protected final static String PARAM_TAG_MUNICIPALITY = "tag_municipality";
    protected final static String PARAM_TAG_NEIGHBORHOOD = "tag_neighborhood";
    protected final static String PARAM_TAG_BLOCK = "tag_block";
    protected final static String PARAM_TAG_PLOT = "tag_plot";

    @Override
    public void handleGet(ActionParameters params) throws ActionException {
        try {
            ResponseHelper.writeResponse(params, TagPipeHelper.getTagPipes(params.getUser(), params.getLocale().getLanguage()));
        } catch (Exception ex){
            log.error(org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(ex));
            throw new ActionParamsException("Couldn't get tagpipes");
        }
    }

    @Override
    public void handleDelete(ActionParameters params) throws ActionException {
        // Only admin user
        params.requireAdminUser();

        int tagPipeId = params.getRequiredParamInt(PARAM_TAG_ID);

        try {
            if(tagPipeId>0) {
                ResponseHelper.writeResponse(params, TagPipeHelper.delete(tagPipeId));
            } else {
                throw new ActionParamsException("Couldn't delete tagpipe");
            }
        } catch (Exception ex){
            log.error(org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(ex));
            throw new ActionParamsException("Couldn't delete tagpipe");
        }
    }

    @Override
    public void handlePost(ActionParameters params) throws ActionException {
        // Only admin user
        params.requireAdminUser();

        try {
            TagPipeConfiguration conf = new TagPipeConfiguration(params);
            // tag_type required on POST / INSERT -- can't be changed with PUT / UPDATE
            conf.setTagType(params.getRequiredParam(PARAM_TAG_TYPE));
            JSONObject response = TagPipeHelper.insert(conf);
            ResponseHelper.writeResponse(params, response);
        } catch (Exception ex){
            log.error(org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(ex));
            throw new ActionParamsException("Couldn't insert tagpipe");
        }
    }

    @Override
    public void handlePut(ActionParameters params) throws ActionException {
        // Only admin user
        params.requireAdminUser();

        try {
            TagPipeConfiguration conf = new TagPipeConfiguration(params);
            // tag_id (server created) is required on PUT / UPDATE -- can't be set on POST / INSERT
            conf.setTagId(params.getRequiredParamInt(PARAM_TAG_ID));
            JSONObject response = TagPipeHelper.update(conf);
            ResponseHelper.writeResponse(params, response);
        } catch (Exception ex){
            log.error(org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(ex));
            throw new ActionParamsException("Couldn't update tagpipe");
        }
    }

}
