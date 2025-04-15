package hsy.seutumaisa.actions;

import org.json.JSONException;
import org.json.JSONObject;

import fi.nls.oskari.annotation.OskariActionRoute;
import fi.nls.oskari.control.ActionException;
import fi.nls.oskari.control.ActionParameters;
import fi.nls.oskari.util.ResponseHelper;
import hsy.seutumaisa.helpers.SeutumaisaDBHelper;

@OskariActionRoute("SeutumaisaSearch")
public class SeutumaisaSearch extends SeutumaisaRestActionHandler {

    @Override
    public void handlePost(ActionParameters params) throws ActionException {
        try {
            JSONObject result = SeutumaisaDBHelper.search(params);
            ResponseHelper.writeResponse(params, result);
        } catch (JSONException e) {
            throw new ActionException("Cannot search", e);
        }
    }
}
