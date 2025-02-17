package hsy.seutumaisa.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import fi.nls.oskari.domain.Role;

public class LandmassConfigMunicipality {

    private String id;
    private String label;
    private List<Long> roles;
    private List<Long> adminRoles;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<Long> getRoles() {
        return roles;
    }

    public void setRoles(List<Long> roles) {
        this.roles = roles;
    }

    public List<Long> getAdminRoles() {
        return adminRoles;
    }

    public void setAdminRoles(List<Long> adminRoles) {
        this.adminRoles = adminRoles;
    }

    public static LandmassConfigMunicipality from(LandmassMunicipality municipality, Role[] allRoles) {
        List<Long> roles = new ArrayList<>();
        roles.add(Role.getAdminRole().getId());
        roles.add(getRoleIdByName(allRoles, LandmassHelper.getRoleNameSeutumassaAdmin()));
        roles.add(getRoleIdByName(allRoles, municipality.getRoleName()));
        roles = roles.stream().filter(Objects::nonNull).toList();

        List<Long> adminRoles = new ArrayList<>();
        adminRoles.add(Role.getAdminRole().getId());
        adminRoles.add(getRoleIdByName(allRoles, LandmassHelper.getRoleNameSeutumassaAdmin()));
        adminRoles.add(getRoleIdByName(allRoles, municipality.getAdminRoleName()));
        adminRoles = adminRoles.stream().filter(Objects::nonNull).toList();

        LandmassConfigMunicipality a = new LandmassConfigMunicipality();
        a.setId(municipality.getId());
        a.setLabel(municipality.getLabel());
        a.setRoles(roles);
        a.setAdminRoles(adminRoles);
        return a;
    }

    private static Long getRoleIdByName(Role[] roles, String name) {
        return Arrays.stream(roles)
                .filter(x -> x.getName().equals(name))
                .map(Role::getId)
                .findAny()
                .orElse(null);
    }

}
