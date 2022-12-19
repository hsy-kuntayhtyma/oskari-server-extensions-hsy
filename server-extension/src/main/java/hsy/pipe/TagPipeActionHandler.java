package hsy.pipe;

import org.json.JSONObject;

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

        int tagPipeId = params.getRequiredParamInt(TagPipeConfiguration.PARAM_TAG_ID);

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
            conf.setTagType(params.getRequiredParam(TagPipeConfiguration.PARAM_TAG_TYPE));
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
            conf.setTagId(params.getRequiredParamInt(TagPipeConfiguration.PARAM_TAG_ID));
            JSONObject response = TagPipeHelper.update(conf);
            ResponseHelper.writeResponse(params, response);
        } catch (Exception ex){
            log.error(org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(ex));
            throw new ActionParamsException("Couldn't update tagpipe");
        }
    }

}
