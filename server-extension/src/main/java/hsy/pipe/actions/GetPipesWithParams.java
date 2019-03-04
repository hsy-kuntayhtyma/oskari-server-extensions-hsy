package hsy.pipe.actions;

import hsy.pipe.helpers.Helpers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import fi.nls.oskari.annotation.OskariActionRoute;
import fi.nls.oskari.control.ActionException;
import fi.nls.oskari.control.ActionHandler;
import fi.nls.oskari.control.ActionParameters;
import fi.nls.oskari.log.LogFactory;
import fi.nls.oskari.log.Logger;
import fi.nls.oskari.util.ResponseHelper;

@OskariActionRoute("GetPipesWithParams")
public class GetPipesWithParams extends ActionHandler {

	private final Logger LOGGER = LogFactory.getLogger(GetPipesWithParams.class);
		
    private static final String PARAM_LAYERS = "layers";
    private static final String PARAM_X = "x";
    private static final String PARAM_Y = "y";
    private static final String PARAM_BBOX = "bbox";
    private static final String PARAM_WIDTH = "width";
    private static final String PARAM_HEIGHT = "height";
    private static final String PARAM_SRS = "srs";
    private static final String PARAM_URL = "url";

	@Override
    public void handleAction(final ActionParameters params) throws ActionException {
		
        final JSONArray data = new JSONArray();
       
	    String wmsUrl = Helpers.getGetFeatureInfoUrlForProxy(params.getHttpParam(PARAM_URL).toString(), params.getHttpParam(PARAM_SRS).toString(),
	    		params.getHttpParam(PARAM_BBOX).toString(), params.getHttpParam(PARAM_WIDTH).toString(), params.getHttpParam(PARAM_HEIGHT).toString(),
	    		params.getHttpParam(PARAM_X).toString(), params.getHttpParam(PARAM_Y).toString(), params.getHttpParam(PARAM_LAYERS).toString());
	    
		URL wms;
		try {
			wms = new URL(wmsUrl);
			URLConnection wmsConn = wms.openConnection();
			wmsConn.setRequestProperty("Accept-Charset", "UTF-8");
			BufferedReader in = new BufferedReader( new InputStreamReader( wmsConn.getInputStream(), "UTF-8" ) );
			
			String inputLine;
	        String html = "";
			        
	        while ((inputLine = in.readLine()) != null) {
	        	html += inputLine;
	        }
	        in.close();
	        
            JSONObject jsoni = new JSONObject(html);
			            
	        ResponseHelper.writeResponse(params, jsoni);
	        
		} catch (JSONException e) {
		    throw new ActionException("Could not populate Response JSON: " + LOGGER.getAsString(data), e);
		} catch (MalformedURLException e) {
			throw new ActionException("Could not populate Response JSON: " + LOGGER.getAsString(data), e);
		} catch (IOException e) {
			throw new ActionException("Could not populate Response JSON: " + LOGGER.getAsString(data), e);
		}
	}
}