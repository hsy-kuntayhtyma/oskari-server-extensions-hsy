package hsy.pipe;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import fi.nls.oskari.annotation.OskariActionRoute;
import fi.nls.oskari.control.ActionException;
import fi.nls.oskari.control.ActionHandler;
import fi.nls.oskari.control.ActionParameters;
import fi.nls.oskari.util.IOHelper;
import fi.nls.oskari.util.ResponseHelper;

@OskariActionRoute("GetPipesWithParams")
public class GetPipesWithParams extends ActionHandler {

    private static final int MAX_FEATURE_COUNT = 50;

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
	    String wmsUrl = getGetFeatureInfoUrlForProxy(params.getHttpParam(PARAM_URL).toString(), params.getHttpParam(PARAM_SRS).toString(),
	    		params.getHttpParam(PARAM_BBOX).toString(), params.getHttpParam(PARAM_WIDTH).toString(), params.getHttpParam(PARAM_HEIGHT).toString(),
	    		params.getHttpParam(PARAM_X).toString(), params.getHttpParam(PARAM_Y).toString(), params.getHttpParam(PARAM_LAYERS).toString());
		try {
		    URL wms = new URL(wmsUrl);
			URLConnection wmsConn = wms.openConnection();
			wmsConn.setRequestProperty("Accept-Charset", "UTF-8");

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try (InputStream in = wmsConn.getInputStream()) {
			    IOHelper.copy(in, baos);
			}
	        ResponseHelper.writeResponse(params, 200, ResponseHelper.CONTENT_TYPE_JSON_UTF8, baos);
		} catch (IOException e) {
			throw new ActionException("Could not populate Response", e);
		}
	}

   private static String getGetFeatureInfoUrlForProxy(String url, String projection, String bbox, String width, String height, String x, String y, String layerName) {
        String wmsUrl = url+"?SERVICE=WMS"
            +"&VERSION=1.1.1&"
            +"REQUEST=GetFeatureInfo"
            +"&SRS="+projection
            +"&BBOX="+bbox
            +"&WIDTH="+width
            +"&HEIGHT="+height
            +"&QUERY_LAYERS="+layerName
            +"&X="+Math.round(Float.parseFloat(x))
            +"&Y="+Math.round(Float.parseFloat(y))
            +"&LAYERS="+layerName
            +"&FEATURE_COUNT="+MAX_FEATURE_COUNT
            +"&INFO_FORMAT=application/json"
            +"&EXCEPTIONS=application/vnd.ogc.se_xml"
            +"&BUFFER=10";
        return wmsUrl;
    }

}