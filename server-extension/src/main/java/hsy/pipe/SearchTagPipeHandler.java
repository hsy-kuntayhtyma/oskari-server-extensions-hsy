package hsy.pipe;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import fi.nls.oskari.annotation.OskariActionRoute;
import fi.nls.oskari.control.ActionException;
import fi.nls.oskari.control.ActionParameters;
import fi.nls.oskari.control.ActionParamsException;
import fi.nls.oskari.control.RestActionHandler;
import fi.nls.oskari.log.LogFactory;
import fi.nls.oskari.log.Logger;
import fi.nls.oskari.service.OskariComponentManager;
import fi.nls.oskari.util.ResponseHelper;

@OskariActionRoute("SearchTagPipe")
public class SearchTagPipeHandler extends RestActionHandler {

    private static final Logger log = LogFactory.getLogger(SearchTagPipeHandler.class);

    private final TagPipeConfigurationService tagpipeService = OskariComponentManager.getComponentOfType(TagPipeConfigurationService.class);

    @Override
    public void handleGet(ActionParameters params) throws ActionException {
        try {
            ResponseHelper.writeResponse(params, getTagPipes());
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
                ResponseHelper.writeResponse(params, delete(tagPipeId));
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
            JSONObject response = insert(conf);
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
            JSONObject response = update(conf);
            ResponseHelper.writeResponse(params, response);
        } catch (Exception ex){
            log.error(org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(ex));
            throw new ActionParamsException("Couldn't update tagpipe");
        }
    }

    private JSONObject getTagPipes() throws JSONException {
        JSONObject job = new JSONObject();
        List<TagPipeConfiguration> tagpipes = tagpipeService.findTagPipes();
        JSONArray tagpipesJSONArray = new JSONArray();

        for(int i=0;i<tagpipes.size();i++){
            TagPipeConfiguration tagpipe = tagpipes.get(i);
            JSONObject tagpipeJSON = tagpipe.getAsJSONObject();
            tagpipesJSONArray.put(tagpipeJSON);
        }

        job.put("tagpipes", tagpipesJSONArray);

        return job;
    }

    private JSONObject delete(final int tagPipeId) {
        JSONObject job = new JSONObject();
        try{
            tagpipeService.delete(tagPipeId);
            job.put("success", true);
        } catch (Exception e) {
            try{
                job.put("success", false);
            } catch (Exception ex) {}
        }
        return job;
    }

    private JSONObject insert(final TagPipeConfiguration tagpipe) {
        JSONObject job = new JSONObject();
        try{
            int newId = tagpipeService.insert(tagpipe);
            job.put("success", newId > 0);
        } catch (Exception e) {
            log.error(org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
            try{
                job.put("success", false);
            } catch (Exception ex) {}
        }
        return job;
    }

    private JSONObject update(final TagPipeConfiguration tagpipe) {
        JSONObject job = new JSONObject();
        try{
            tagpipeService.update(tagpipe);
            job.put("success", true);
        } catch (Exception e) {
            log.error(org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
            try{
                job.put("success", false);
            } catch (Exception ex) {}
        }
        return job;
    }

}
