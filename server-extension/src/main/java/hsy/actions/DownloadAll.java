package hsy.actions;

import hsy.helpers.Helpers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Properties;

import hsy.helpers.DownloadDetail;
import hsy.helpers.SendDownloadDetailsToEmailThread;
import hsy.helpers.downloadDetails2pojo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import fi.nls.oskari.annotation.OskariActionRoute;
import fi.nls.oskari.control.ActionException;
import fi.nls.oskari.control.ActionHandler;
import fi.nls.oskari.control.ActionParameters;
import fi.nls.oskari.log.LogFactory;
import fi.nls.oskari.log.Logger;
import fi.nls.oskari.map.data.service.GetGeoPointDataService;
import fi.nls.oskari.map.layer.OskariLayerService;
import fi.nls.oskari.map.layer.OskariLayerServiceIbatisImpl;
import fi.nls.oskari.map.myplaces.service.GeoServerProxyService;
import fi.nls.oskari.util.ResponseHelper;

@OskariActionRoute("DownloadAll")
public class DownloadAll extends ActionHandler {

	private Logger log = LogFactory.getLogger(DownloadAll.class);
		
    private static final String PARAM_DOWNLOAD_DETAILS = "downloadDetails";
    private static final String PARAM_USER_DETAILS = "userDetails";

	@Override
    public void handleAction(final ActionParameters params) throws ActionException {
	     
		JSONObject job = new JSONObject();
		String downloadDetails = params.getHttpParam(PARAM_DOWNLOAD_DETAILS).toString();
		ArrayList<DownloadDetail> dds = new ArrayList<DownloadDetail>();
		
		String strUserDetails = params.getHttpParam(PARAM_USER_DETAILS).toString();			
		JSONObject userDetails = new JSONObject();
					
		try {

			userDetails = new JSONObject(strUserDetails);			
			new SendDownloadDetailsToEmailThread(ddArray, userDetails).start();
			job.put("success", true);
		} catch (Exception e) {
			throw new ActionException("Could not handle DownloadAll request: ", e);
		}
		
		ResponseHelper.writeResponse(params, job);
	}
}
