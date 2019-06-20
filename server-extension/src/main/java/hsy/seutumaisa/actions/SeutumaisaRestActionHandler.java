package hsy.seutumaisa.actions;

import fi.nls.oskari.control.ActionDeniedException;
import fi.nls.oskari.control.ActionException;
import fi.nls.oskari.control.RestActionHandler;
import fi.nls.oskari.util.PropertyUtil;

public class SeutumaisaRestActionHandler extends RestActionHandler {
    private final static String PROPERTY_MODULES = "db.additional.modules";

    public void requireSeutumaisaConfigured() throws ActionException {
        String modules = PropertyUtil.get(PROPERTY_MODULES, "");
        if(!modules.contains("seutumaisa")) {
            throw new ActionDeniedException("Not seutumaisa configured");
        }

    }
}
