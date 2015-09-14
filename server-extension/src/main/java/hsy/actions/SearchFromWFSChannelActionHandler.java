package tampere.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tampere.domain.WFSSearchChannelsConfiguration;
import tampere.helpers.SearchWFSChannelHelper;
import fi.mml.portti.service.search.SearchCriteria;
import fi.nls.oskari.SearchWorker;
import fi.nls.oskari.annotation.OskariActionRoute;
import fi.nls.oskari.control.ActionException;
import fi.nls.oskari.control.ActionHandler;
import fi.nls.oskari.control.ActionParameters;
import fi.nls.oskari.control.ActionParamsException;
import fi.nls.oskari.domain.map.OskariLayer;
import fi.nls.oskari.log.LogFactory;
import fi.nls.oskari.log.Logger;
import fi.nls.oskari.map.layer.OskariLayerService;
import fi.nls.oskari.map.layer.OskariLayerServiceIbatisImpl;
import fi.nls.oskari.util.PropertyUtil;
import fi.nls.oskari.util.ResponseHelper;

@OskariActionRoute("GetWfsSearchResult")
public class SearchFromWFSChannelActionHandler extends ActionHandler {
	
	private static final Logger log = LogFactory.getLogger(SearchFromWFSChannelActionHandler.class);
    private static final String PARAM_SEARCH_KEY = "searchKey";
    private static final String PARAM_EPSG_KEY = "epsg";
    private static final String PARAM_CHANNELIDS_KEY = "channelIds";
    public static final String PARAM_CHANNELS = "channels";
    private static final String DEFAULT_SRS = "EPSG:3067";
    private static final String DEFAULT_VERSION = "1.1.0";

    private String[] channels = new String[0];

    public void init() {
        channels = PropertyUtil.getCommaSeparatedList("actionhandler.GetSearchResult.channels");
    }

    public void handleAction(final ActionParameters params) throws ActionException {
        final String search = params.getHttpParam(PARAM_SEARCH_KEY);
        if (search == null) {
            throw new ActionParamsException("Search string was null");
        }
        final String epsg = params.getHttpParam(PARAM_EPSG_KEY);
        
        JSONArray channelIds;
        List<WFSSearchChannelsConfiguration> channelsParams = new ArrayList<WFSSearchChannelsConfiguration>();
        
		try {
			
			if(params.getHttpParam(PARAM_CHANNELIDS_KEY) != null){
				channelIds = new JSONArray(params.getHttpParam(PARAM_CHANNELIDS_KEY));
			}else{
				channelIds = SearchWFSChannelHelper.getDefaultChannelsIds();
			}
			
			List<WFSSearchChannelsConfiguration> channels = SearchWFSChannelHelper.getChannelById(channelIds);
			
			for (int i = 0; i < channels.size(); i++) {
				WFSSearchChannelsConfiguration channel = channels.get(i);
				List<String> layerIds = new ArrayList<String>();
				layerIds.add(String.valueOf(channel.getWFSLayerId())); 
				
				OskariLayerService mapLayerService = new OskariLayerServiceIbatisImpl();
				OskariLayer oskariLayer = mapLayerService.find(channel.getWFSLayerId());

				if(oskariLayer != null){

					channel.setUsername(oskariLayer.getUsername());
					channel.setPassword(oskariLayer.getPassword());

					channel.setLayerName(oskariLayer.getName());
					if(oskariLayer.getSrs_name() != null && !oskariLayer.getSrs_name().isEmpty()){
						channel.setSrs(oskariLayer.getSrs_name());
					} else {
						channel.setSrs(DEFAULT_SRS);
					}
					if(oskariLayer.getVersion() != null && !oskariLayer.getVersion().isEmpty()){
						channel.setVersion(oskariLayer.getVersion());
					} else {
						channel.setVersion(DEFAULT_VERSION);
					}
					channel.setUrl(oskariLayer.getUrl());
					
					//FIXME Jatkokehitysta varten jos pitaa hakea tason kielistetty nimi
					//channel.setRealName(layers.getJSONObject(0).getJSONObject(PARAM_REALNAME));
					channelsParams.add(channel);
				}
				
			}
			
		} catch (JSONException ex) {
			 log.error(org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(ex));    		 
	   		 throw new ActionParamsException("Couldn't get WFS search channelsIds");
		}
		
        final String error = SearchWorker.checkLegalSearch(search);

        if (!SearchWorker.STR_TRUE.equals(error)) {
            // write error message key
            ResponseHelper.writeResponse(params, error);
        } else {
            final Locale locale = params.getLocale();

            final SearchCriteria sc = new SearchCriteria();
            sc.setSearchString(search);
            sc.setSRS(epsg);  // eg. EPSG:3067
            sc.addParam(PARAM_CHANNELS, channelsParams);

            sc.setLocale(locale.getLanguage());

            for(String channelId : channels) {
                sc.addChannel(channelId);
            }
            final JSONObject result = SearchWorker.doSearch(sc);
            ResponseHelper.writeResponse(params, result);
        }
    }
}