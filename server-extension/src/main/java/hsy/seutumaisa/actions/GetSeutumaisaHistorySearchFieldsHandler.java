package hsy.seutumaisa.actions;


import fi.nls.oskari.annotation.OskariActionRoute;
import fi.nls.oskari.control.ActionException;
import fi.nls.oskari.control.ActionParameters;
import fi.nls.oskari.log.LogFactory;
import fi.nls.oskari.log.Logger;
import fi.nls.oskari.util.ResponseHelper;
import hsy.seutumaisa.helpers.SeutumaisaHistoryDBHelper;

import org.json.JSONArray;
import org.json.JSONException;

@OskariActionRoute("GetSeutumaisaHistorySearchFields")
public class GetSeutumaisaHistorySearchFieldsHandler extends SeutumaisaRestActionHandler {
    private static Logger LOG = LogFactory.getLogger(GetSeutumaisaHistorySearchFieldsHandler.class);

    @Override
    public void handleGet(ActionParameters params) throws ActionException {
        requireSeutumaisaConfigured();

        try {
            JSONArray fields = SeutumaisaHistoryDBHelper.getHistorySearchFields();
            ResponseHelper.writeResponse(params, fields);
        } catch (JSONException e) {
            LOG.error("Error for getting seutumaisa history fields", e);
            throw new ActionException("Cannot get history fields");
        }
    }
}
