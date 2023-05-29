package hsy.seutumaisa.actions;


import fi.nls.oskari.annotation.OskariActionRoute;
import fi.nls.oskari.control.ActionException;
import fi.nls.oskari.control.ActionParameters;
import fi.nls.oskari.log.LogFactory;
import fi.nls.oskari.log.Logger;
import fi.nls.oskari.util.ResponseHelper;
import hsy.seutumaisa.helpers.SeutumaisaDBHelper;
import org.json.JSONArray;
import org.json.JSONException;

@OskariActionRoute("GetSeutumaisaSearchFields")
public class GetSeutumaisaSearchFieldsHandler extends SeutumaisaRestActionHandler {
    private static Logger LOG = LogFactory.getLogger(GetSeutumaisaSearchFieldsHandler.class);

    @Override
    public void handleGet(ActionParameters params) throws ActionException {
        try {
            JSONArray fields = SeutumaisaDBHelper.getSearchFields();
            ResponseHelper.writeResponse(params, fields);
        } catch (JSONException e) {
            LOG.error("Error for getting seutumaisa fields", e);
            throw new ActionException("Cannot get fields");
        }
    }
}
