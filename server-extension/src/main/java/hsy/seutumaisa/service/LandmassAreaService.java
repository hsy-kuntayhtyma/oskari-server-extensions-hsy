package hsy.seutumaisa.service;

import java.util.List;

import fi.nls.oskari.service.OskariComponent;
import hsy.seutumaisa.domain.LandmassArea;

public abstract class LandmassAreaService extends OskariComponent {

    public abstract List<LandmassArea> getAreasByCoordinate(double lon, double lat);
    public abstract LandmassArea getAreaById(int id);
    public abstract void save(LandmassArea area);
    public abstract void update(LandmassArea area);
    public abstract void delete(int id);

}
