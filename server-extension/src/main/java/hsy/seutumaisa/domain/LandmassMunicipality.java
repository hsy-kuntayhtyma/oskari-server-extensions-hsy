package hsy.seutumaisa.domain;

import java.util.Arrays;
import java.util.Optional;

public enum LandmassMunicipality {

    ESPOO("049", "Espoo"),
    HELSINKI("091", "Helsinki"),
    VANTAA("092", "Vantaa");

    private final String id;
    private final String label;

    public static Optional<LandmassMunicipality> byId(String id) {
        return Arrays.stream(values()).filter(x -> x.id.equals(id)).findAny();
    }

    private LandmassMunicipality(String id, String label) {
        this.id = id;
        this.label = label;
    }

    public String getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public String getRoleName() {
        return LandmassHelper.getRoleName(label);
    }

    public String getAdminRoleName() {
        return LandmassHelper.getAdminRoleName(label);
    }

}
