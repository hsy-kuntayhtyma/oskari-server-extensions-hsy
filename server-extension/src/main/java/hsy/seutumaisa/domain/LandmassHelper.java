package hsy.seutumaisa.domain;

public final class LandmassHelper {

    private static final String ROLE_SEUTUMAISA_PREFIX = "SeutuMaisa_";
    private static final String ROLE_ADMIN_SUFFIX = "_Admin";

    private LandmassHelper() {}

    static String getRoleName(String municipality) {
        return ROLE_SEUTUMAISA_PREFIX + municipality;
    }

    static String getAdminRoleName(String municipality) {
        return getRoleName(municipality) + ROLE_ADMIN_SUFFIX;
    }

    public static String getRoleNameSeutumassaAdmin() {
        return "SeutuMassa_HSY";
    }

}
