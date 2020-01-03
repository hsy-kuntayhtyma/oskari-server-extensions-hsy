package hsy.pipe.helpers;

import hsy.pipe.tagpipe.TagPipeConfigurationService;
import hsy.pipe.tagpipe.TagPipeConfigurationServiceIbatisImpl;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import hsy.pipe.domain.TagPipeConfiguration;
import fi.nls.oskari.domain.User;
import fi.nls.oskari.log.LogFactory;
import fi.nls.oskari.log.Logger;

public class TagPipeHelper {
	
	private static final Logger log = LogFactory.getLogger(TagPipeHelper.class);
	private static TagPipeConfigurationService tagpipeService = new TagPipeConfigurationServiceIbatisImpl();
	
	/**
	 * Get tagpipes
	 * @param string 
	 * @param user 
	 * @return
	 */
	public static JSONObject getTagPipes(User user, String lang) throws JSONException{
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
	
	/**
	 * Get tagpipe by id
	 * @param tagPipeIds
	 * @return
	 * @throws JSONException 
	 */
	public static List<TagPipeConfiguration> getTagPipeById(JSONArray tagPipeIds) throws JSONException{

		List<TagPipeConfiguration> tagpipes = new ArrayList<TagPipeConfiguration>();
		for (int i = 0; i < tagPipeIds.length(); i++) {
			tagpipes.add(tagpipeService.findTagPipeById(tagPipeIds.getInt(i)));
		}
	   	 
	   	return tagpipes;
	}
	
	/**
	 * Delete selected tagpipe
	 * @param tagpipeId
	 */
	public static JSONObject delete(final int tagPipeId) {
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
	
	/**
	 * Add tagpipe
	 * @param tagpipe
	 */
	public static JSONObject insert(final TagPipeConfiguration tagpipe) {
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
	
	/**
	 * Update tagpipe
	 * @param tagpipe
	 */
	public static JSONObject update(final TagPipeConfiguration tagpipe) {
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
