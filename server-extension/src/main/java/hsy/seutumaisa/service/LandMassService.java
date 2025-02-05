package hsy.seutumaisa.service;

import java.util.List;

import fi.nls.oskari.service.OskariComponent;
import hsy.seutumaisa.domain.LandMassArea;

public abstract class LandMassService extends OskariComponent {

    public abstract List<LandMassArea> getAreasByCoordinate(double lon, double lat);
    public abstract LandMassArea getAreaById(long id);
    public abstract void save(LandMassArea area);
    public abstract void update(LandMassArea area);
    public abstract void delete(long id);

}
