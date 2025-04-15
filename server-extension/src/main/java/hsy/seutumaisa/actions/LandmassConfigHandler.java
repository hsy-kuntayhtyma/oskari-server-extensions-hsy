package hsy.seutumaisa.actions;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import fi.nls.oskari.annotation.OskariActionRoute;
import fi.nls.oskari.control.ActionException;
import fi.nls.oskari.control.ActionParameters;
import fi.nls.oskari.domain.Role;
import fi.nls.oskari.log.LogFactory;
import fi.nls.oskari.log.Logger;
import fi.nls.oskari.service.ServiceException;
import fi.nls.oskari.service.ServiceRuntimeException;
import fi.nls.oskari.service.UserService;
import hsy.seutumaisa.domain.LandmassConfig;
import hsy.seutumaisa.domain.LandmassConfigMunicipality;
import hsy.seutumaisa.domain.LandmassMunicipality;

@OskariActionRoute("LandmassConfig")
public class LandmassConfigHandler extends SeutumaisaRestActionHandler {

    private static Logger LOG = LogFactory.getLogger(LandmassConfigHandler.class);

    private UserService service;

    @Override
    public void init() {
        super.init();
        try {
            service = UserService.getInstance();
        } catch (Exception e) {
            LOG.error(e, "Failed to get UserService");
        }
    }

    @Override
    public void handleGet(ActionParameters params) throws ActionException {
        params.requireLoggedInUser();

        Role[] allRoles = getAllRoles(service);

        List<LandmassConfigMunicipality> municipalities = Arrays.stream(LandmassMunicipality.values())
                .map(x -> LandmassConfigMunicipality.from(x, allRoles))
                .collect(Collectors.toList());

        LandmassConfig config = new LandmassConfig();
        config.setMunicipalities(municipalities);

        writeResponse(params, config);
    }

    private static Role[] getAllRoles(UserService service) {
        try {
            return service.getRoles();
        } catch (ServiceException e) {
            throw new ServiceRuntimeException("Failed to get roles", e);
        }
    }

}
