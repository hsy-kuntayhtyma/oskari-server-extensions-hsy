package hsy.seutumaisa.actions;

import fi.nls.oskari.control.ActionDeniedException;
import fi.nls.oskari.control.ActionException;
import fi.nls.oskari.control.ActionParameters;
import fi.nls.oskari.control.RestActionHandler;
import fi.nls.oskari.util.PropertyUtil;

public class SeutumaisaRestActionHandler extends RestActionHandler {
    private final static String PROPERTY_ENABLED = "seutumassa.enabled";

    public void preProcess(ActionParameters params) throws ActionException {
        if (!PropertyUtil.getOptional(PROPERTY_ENABLED, false)) {
            throw new ActionDeniedException("Not seutumaisa configured");
        }
    }
}
