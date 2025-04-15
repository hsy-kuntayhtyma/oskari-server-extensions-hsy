package hsy.seutumaisa.domain;

import java.util.List;

public class LandmassConfig {

    private List<LandmassConfigMunicipality> municipalities;

    public List<LandmassConfigMunicipality> getMunicipalities() {
        return municipalities;
    }

    public void setMunicipalities(List<LandmassConfigMunicipality> municipalities) {
        this.municipalities = municipalities;
    }

}
