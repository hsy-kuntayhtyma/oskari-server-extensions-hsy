package hsy.seutumaisa.service;

import java.util.List;

import fi.nls.oskari.service.OskariComponent;
import hsy.seutumaisa.domain.LandmassProject;

public abstract class LandmassProjectService extends OskariComponent {

    public abstract LandmassProject getById(long id);
    public abstract List<LandmassProject> getAll();
    public abstract void save(LandmassProject project);
    public abstract void update(LandmassProject project);
    public abstract void delete(long id);

}
