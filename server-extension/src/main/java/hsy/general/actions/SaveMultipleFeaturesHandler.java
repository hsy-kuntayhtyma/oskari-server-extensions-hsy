package hsy.general.actions;
import fi.nls.oskari.annotation.OskariActionRoute;
import fi.nls.oskari.control.ActionException;
import fi.nls.oskari.control.ActionParameters;
import fi.nls.oskari.control.feature.AbstractFeatureHandler;
import fi.nls.oskari.domain.map.OskariLayer;
import fi.nls.oskari.log.LogFactory;
import fi.nls.oskari.log.Logger;
import fi.nls.oskari.util.JSONHelper;
import fi.nls.oskari.util.ResponseHelper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@OskariActionRoute("SaveMultipleFeatures")
public class SaveMultipleFeaturesHandler extends AbstractFeatureHandler {
    private static Logger log = LogFactory.getLogger(SaveMultipleFeaturesHandler.class);

    @Override
    public void handlePost(ActionParameters params) throws ActionException {
        params.requireLoggedInUser();

        JSONArray featuresJSONArray = null;

        try {
            featuresJSONArray = new JSONArray(params.getHttpParam("featureData"));
        } catch (JSONException ex) {
            log.error(ex, "JSON processing error");
            throw new ActionException("JSON processing error", ex);
        }

        for(int i=0; i < featuresJSONArray.length(); i++) {
            try {
                JSONObject jsonObject = featuresJSONArray.getJSONObject(i);

                OskariLayer layer = getLayer(jsonObject.optString("layerId"));

                if (!canEdit(layer, params.getUser())) {
                    log.warn("User doesn't have edit permission for layer: " + layer.getId());
                    continue;
                }


                String srsName = JSONHelper.getStringFromJSON(jsonObject, "srsName", "http://www.opengis.net/gml/srs/epsg.xml#3067");

                // TODO: rewrite to use wfs-t related code under myplaces OR atleast using an xml lib
                StringBuilder requestData = new StringBuilder(
                        "<wfs:Transaction service='WFS' version='1.1.0' " +
                                "xmlns:ogc='http://www.opengis.net/ogc' " +
                                "xmlns:wfs='http://www.opengis.net/wfs'>" +
                                "<wfs:Update typeName='" + layer.getName() + "'>");
                JSONArray jsonArray = jsonObject.getJSONArray("featureFields");
                for (int j = 0; j < jsonArray.length(); j++) {
                    requestData.append("<wfs:Property><wfs:Name>" + jsonArray.getJSONObject(j).getString("key") +
                            "</wfs:Name><wfs:Value>" + jsonArray.getJSONObject(j).getString("value") +
                            "</wfs:Value></wfs:Property>");
                }

                requestData.append("<ogc:Filter><ogc:FeatureId fid='" + jsonObject.getString("featureId") + "'/></ogc:Filter></wfs:Update></wfs:Transaction>");
                
                String responseString = postPayload(layer.getUsername(), layer.getPassword(), requestData.toString(), getURLForNamespace(layer.getName(),layer.getUrl()));

                if (responseString.indexOf("Exception") > -1) {
                    ResponseHelper.writeResponse(params, "Exception");
                } else if (responseString.indexOf("<wfs:totalUpdated>1</wfs:totalUpdated>") > -1) {
                    ResponseHelper.writeResponse(params, "");
                }
            } catch (JSONException ex) {
                log.error(ex, "JSON processing error");
                throw new ActionException("JSON processing error", ex);
            }
        }
    }
}

