package hsy.seutumaisa.service;

import java.util.List;

import fi.nls.oskari.service.OskariComponent;
import hsy.seutumaisa.domain.LandMassArea;

public abstract class LandMassAreaService extends OskariComponent {

    public abstract List<LandMassArea> getByCoordinate(double lon, double lat);
    public abstract long save(LandMassArea area);
    public abstract void update(LandMassArea area);
    public abstract void delete(long id);

}
