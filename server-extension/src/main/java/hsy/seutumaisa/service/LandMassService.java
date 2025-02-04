package hsy.seutumaisa.service;

import java.util.List;

import fi.nls.oskari.service.OskariComponent;
import hsy.seutumaisa.domain.LandMassArea;
import hsy.seutumaisa.domain.LandMassData;
import hsy.seutumaisa.domain.Person;

public abstract class LandMassService extends OskariComponent {

    public abstract List<LandMassArea> getAreasByCoordinate(double lon, double lat);
    public abstract List<LandMassData> getDataByAreaId(long areaId);
    public abstract Person getPersonById(long personId);

    public abstract LandMassArea getAreaById(long id);
    public abstract long save(LandMassArea area);
    public abstract void update(LandMassArea area);
    public abstract void delete(long id);

}
