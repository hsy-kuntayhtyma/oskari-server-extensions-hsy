package hsy.actions;

import hsy.helpers.Helpers;

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
import fi.nls.oskari.domain.User;
import fi.nls.oskari.log.LogFactory;
import fi.nls.oskari.log.Logger;
import fi.nls.oskari.map.data.service.GetGeoPointDataService;
import fi.nls.oskari.map.layer.OskariLayerService;
import fi.nls.oskari.map.layer.OskariLayerServiceIbatisImpl;
import fi.nls.oskari.map.myplaces.service.GeoServerProxyService;
import fi.nls.oskari.util.ResponseHelper;

@OskariActionRoute("GetFeatureForCropping")
public class GetFeatureForCropping extends ActionHandler {

	private final OskariLayerService mapLayerService = new OskariLayerServiceIbatisImpl();
	private final GetGeoPointDataService geoPointService = new GetGeoPointDataService();
    private final GeoServerProxyService myplacesService = new GeoServerProxyService();

	private Logger log = LogFactory.getLogger(GetFeatureForCropping.class);
		
    private static final String PARAM_LAYERS = "layers";
    private static final String PARAM_X = "x";
    private static final String PARAM_Y = "y";
    private static final String PARAM_BBOX = "bbox";
    private static final String PARAM_WIDTH = "width";
    private static final String PARAM_HEIGHT = "height";
    private static final String PARAM_SRS = "srs";
    private static final String PARAM_URL = "http://10.20.0.4:9902/geoserver/taustakartat_ja_aluejaot/wms";

	@Override
    public void handleAction(final ActionParameters params) throws ActionException {
	     
		final String layerIds = params.getHttpParam(PARAM_LAYERS);
		System.out.println("TULEE JES: ON VAIN VOITTO!!! - "+layerIds);
		final String[] layerIdsArr = layerIds.split(",");
		
        final User user = params.getUser();
//        final double lat = ConversionHelper.getDouble(params.getHttpParam(PARAM_LAT), -1);
//        final double lon = ConversionHelper.getDouble(params.getHttpParam(PARAM_LON), -1);
//        final int zoom = ConversionHelper.getInt(params.getHttpParam(PARAM_ZOOM), 0);
        
        final JSONArray data = new JSONArray();
//		JSONObject geojs = new JSONObject();
       
	    String wmsUrl = Helpers.getGetFeatureInfoUrlForProxy(PARAM_URL, params.getHttpParam(PARAM_SRS).toString(),
	    		params.getHttpParam(PARAM_BBOX).toString(), params.getHttpParam(PARAM_WIDTH).toString(), params.getHttpParam(PARAM_HEIGHT).toString(),
	    		params.getHttpParam(PARAM_X).toString(), params.getHttpParam(PARAM_Y).toString(), params.getHttpParam(PARAM_LAYERS).toString());
		
				System.out.println(wmsUrl);
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
		    throw new ActionException("Could not populate Response JSON: " + log.getAsString(data), e);
		} catch (MalformedURLException e) {
			throw new ActionException("Could not populate Response JSON: " + log.getAsString(data), e);
		} catch (IOException e) {
			throw new ActionException("Could not populate Response JSON: " + log.getAsString(data), e);
		}
	}
}