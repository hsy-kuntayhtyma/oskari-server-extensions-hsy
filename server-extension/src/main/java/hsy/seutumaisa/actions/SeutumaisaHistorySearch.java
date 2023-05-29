package hsy.seutumaisa.actions;

import fi.nls.oskari.annotation.OskariActionRoute;
import fi.nls.oskari.control.ActionException;
import fi.nls.oskari.control.ActionParameters;
import fi.nls.oskari.log.LogFactory;
import fi.nls.oskari.log.Logger;
import fi.nls.oskari.util.ResponseHelper;
import hsy.seutumaisa.helpers.SeutumaisaHistoryDBHelper;

import org.json.JSONException;
import org.json.JSONObject;

@OskariActionRoute("SeutumaisaHistorySearch")
public class SeutumaisaHistorySearch extends SeutumaisaRestActionHandler {
    private static Logger LOG = LogFactory.getLogger(SeutumaisaHistorySearch.class);

    @Override
    public void handlePost(ActionParameters params) throws ActionException {
        try {
            JSONObject result = SeutumaisaHistoryDBHelper.search(params);
            ResponseHelper.writeResponse(params, result);
        } catch (JSONException e) {
            LOG.error("Error for seutumaisa history search", e);
            throw new ActionException("Cannot search history");
        }
    }
}
