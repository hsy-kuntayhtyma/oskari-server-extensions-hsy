package flyway.ammassuo;

import fi.nls.oskari.control.ActionException;
import fi.nls.oskari.domain.Role;
import fi.nls.oskari.domain.User;
import fi.nls.oskari.domain.map.OskariLayer;
import fi.nls.oskari.domain.map.view.View;
import fi.nls.oskari.log.LogFactory;
import fi.nls.oskari.log.Logger;
import fi.nls.oskari.map.view.ViewService;
import fi.nls.oskari.map.view.AppSetupServiceMybatisImpl;
import fi.nls.oskari.service.ServiceException;
import fi.nls.oskari.service.UserService;
import fi.nls.oskari.user.DatabaseUserService;
import fi.nls.oskari.user.MybatisRoleService;
import fi.nls.oskari.util.PropertyUtil;
import helpers.LayerHelper;
import org.flywaydb.core.api.migration.jdbc.JdbcMigration;
import org.oskari.permissions.PermissionService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import fi.nls.oskari.service.OskariComponentManager;
import java.sql.*;
import java.util.List;

public class V1_00_4__add_more_ammassuo_roles implements JdbcMigration {
    private static final ViewService VIEW_SERVICE = new AppSetupServiceMybatisImpl();
    private static final MybatisRoleService ROLE_SERVICE = new MybatisRoleService();
    private static final PermissionService  PERMISSIONS_SERVICE = OskariComponentManager.getComponentOfType(PermissionService.class);
    private static final Logger LOG = LogFactory.getLogger(V1_00_4__add_more_ammassuo_roles.class);

    private enum Roles
    {
        KATSELIJAT(LayerHelper.ROLE_AMMASSUO_KATSELIJAT, "ammassuo_katselija"),
        PIIRTO_OIKEUS_BIOJATE(LayerHelper.ROLE_AMMASSUO_PIIRTO_OIKEUS_BIOJATE, "ammassuo_biojate"),
        PIIRTO_OIKEUS_JATTEENJALOSTUS(LayerHelper.ROLE_AMMASSUO_PIIRTO_OIKEUS_JATTEENJALOSTUS, "ammassuo_jatteenjalostus"),
        PIIRTO_OIKEUS_PIMA(LayerHelper.ROLE_AMMASSUO_PIIRTO_OIKEUS_PIMA, "ammassuo_pima"),
        PAAKAYTTAJA(LayerHelper.ROLE_AMMASSUO_PAAKAYTTAJA, "ammassuo_paakayttaja");

        private String roleName;
        private String username;

        Roles(String roleName, String username) {
            this.roleName = roleName;
            this.username = username;
        }

        public String getName() {
            return this.roleName;
        }

        public String getUsername() {
            return this.username;
        }
    }


    public void migrate(Connection connection) throws Exception, JSONException {
        // configure the view that should be used as default options
        boolean skip = PropertyUtil.getOptional("flyway.ammassuo.V1_00_4.skip", false);
        if(skip) {
            return;
        }
        try {
            View view = VIEW_SERVICE.getViewWithConf("Ämmässuo näkymä");
            StringBuilder sbRoles = new StringBuilder();


            for(Roles role : Roles.values())
            {
                long roleId = addNewRole(role.getName());
                sbRoles.append(", " + role.getName());
            }
            LOG.info("Add oskari-ext.properties -file followings:\n");
            for(Roles role : Roles.values())
            {
                LOG.info("\tview.default."+role.getName()+"=" + view.getId() + "\n");
            }
            LOG.info("\tview.default.roles=Admin, User, Guest, Ammassuo, " + sbRoles + "\n",
                    "\tactionhandler.GetAppSetup.dynamic.bundle.content-editor.roles = Ammassuo, " + sbRoles);


            // add role rights to maplayers
            // first set basemap layers to view rights
            addLayerPermissions(getDefaultRolePermissionsJSON(), getBasemapLayers(connection), connection);

            // then customize rigths per role
            // Biojäte
            addLayerPermissions(getEditRolePermissionsJSON(LayerHelper.ROLE_AMMASSUO_PIIRTO_OIKEUS_BIOJATE), getBiojateMapLayers(connection), connection);

            // Jätteenjalostus
            addLayerPermissions(getEditRolePermissionsJSON(LayerHelper.ROLE_AMMASSUO_PIIRTO_OIKEUS_JATTEENJALOSTUS), getJatteenjalostusMapLayers(connection), connection);

            // Pima
            addLayerPermissions(getEditRolePermissionsJSON(LayerHelper.ROLE_AMMASSUO_PIIRTO_OIKEUS_PIMA), getPimaMapLayers(connection), connection);

            // add default users
            addDefaultUsers();



        } catch (Exception e) {
            LOG.warn(e, "Something went wrong while adding roles and rights!");
            throw e;
        }
    }

    private void addDefaultUsers() throws ServiceException {
        UserService dbService = DatabaseUserService.getInstance();
        MybatisRoleService roleService = new MybatisRoleService();

        for(Roles role : Roles.values())
        {
            String username = role.getUsername();
            String password = PropertyUtil.get("flyway.ammassuo.V1_00_4." + username + ".password" , username);

            User user = new User();
            user.setEmail(username + "@hsy.fi");
            user.setFirstname(username);
            user.setLastname("HSY");
            user.setScreenname(username);
            Role dbRole = roleService.findRoleByName(role.getName());
            String[] roleIds = new String[1];
            roleIds[0] = "" + dbRole.getId();
            dbService.createUser(user, roleIds);
            dbService.setUserPassword(user.getScreenname(), password);
            LOG.info("Added user: " + role.getUsername());

        }
    }

    /**
     * Add layer permissions
     * @param permissions permissions
     * @param layers layers
     */
    public void addLayerPermissions(final JSONObject permissions, final List<OskariLayer> layers, final Connection conn) throws SQLException, JSONException {
        LayerHelper.insertLayerPermissions(permissions, layers, conn);
    }

    /**
     * Add new role
     * @param roleName role name
     * @return role id
     */
    public long addNewRole(String roleName) {
        MybatisRoleService roles = new MybatisRoleService();
        Role role = roles.findRoleByName(roleName);
        if(role == null) {
            Role newRole = new Role();
            newRole.setName(roleName);
            return roles.insert(newRole);
        }
        return -1;
    }

    /**
     * Gets editable role permissions
     * @param editRole role that has editable permission
     * @return permission JSONObject
     * @throws JSONException
     */
    private JSONObject getEditRolePermissionsJSON(String editRole) throws JSONException {
        JSONObject json = new JSONObject();

        for(Roles role : Roles.values())
        {
            String roleName = role.getName();
            JSONArray rights = new JSONArray();
            rights.put("PUBLISH");
            rights.put("VIEW_LAYER");
            rights.put("VIEW_PUBLISHED");
            if(roleName.equals(LayerHelper.ROLE_AMMASSUO_PAAKAYTTAJA) || roleName.equals(editRole)) {
                rights.put("EDIT_LAYER_CONTENT");
            }
            json.put(roleName, rights);
        }
        return json;
    }

    /**
     * Gets editable role permiossions
     * @param editRoles roles that has editable permission
     * @return permission JSONObject
     * @throws JSONException
     */
    private JSONObject getEditRolePermissionsJSON(List<String> editRoles) throws JSONException {
        JSONObject json = new JSONObject();

        for(Roles role : Roles.values())
        {
            String roleName = role.getName();
            JSONArray rights = new JSONArray();
            rights.put("PUBLISH");
            rights.put("VIEW_LAYER");
            rights.put("VIEW_PUBLISHED");
            if(roleName.equals(LayerHelper.ROLE_AMMASSUO_PAAKAYTTAJA) || editRoles.contains(roleName)) {
                rights.put("EDIT_LAYER");
            }
            json.put(roleName, rights);
        }
        return json;
    }

    /**
     * Get default role permissions
     * @return permission JSONObject
     * @throws JSONException
     */
    private JSONObject getDefaultRolePermissionsJSON() throws JSONException {
        JSONObject json = new JSONObject();

        for(Roles role : Roles.values())
        {
            String roleName = role.getName();
            JSONArray rights = new JSONArray();
            rights.put("PUBLISH");
            rights.put("VIEW_LAYER");
            rights.put("VIEW_PUBLISHED");
            if(roleName.equals(LayerHelper.ROLE_AMMASSUO_PAAKAYTTAJA)) {
                rights.put("EDIT_LAYER");
            }
            json.put(roleName, rights);
        }
        return json;
    }

    /**
     * Gets all maplayers
     * @param conn
     * @return
     * @throws SQLException
     */
    private List<OskariLayer> getBasemapLayers(Connection conn) throws SQLException {
        final String sql = "SELECT type, url, name FROM oskari_maplayer WHERE locale LIKE '%Opaskartta PKS%' OR "+
                "locale LIKE '%OpenStreetMap%' OR locale LIKE '%Ortoilmakuva 2015%' OR locale LIKE '%Ortoilmakuva 2017%';";
        return LayerHelper.getMapLayers(conn, sql);
    }

    /**
     * Gets pima editable layers
     * @param conn
     * @return pima editable layers
     * @throws SQLException
     */
    private List<OskariLayer> getPimaMapLayers(Connection conn) throws SQLException, ActionException {

        final String sql = "SELECT type, url, name " +
                "FROM oskari_maplayer " +
                "WHERE name='Ammassuo:AM_JAE_pilaantuneet_maat' OR " +
                "name='Ammassuo:AM_JAE_pima_ja_jate' OR " +
                "name='Ammassuo:AM_JAE_vaaralliset_maat' OR " +
                "name='Ammassuo:AM_JAE_voimakkaasti_pilaantuneet_maat';";
        List<OskariLayer> list = LayerHelper.getMapLayers(conn, sql);
        if(list.size() != 4) {
            throw new ActionException("Cannot get all pima editable layers");
        }
        return list;
    }

    /**
     * Gets jätteenjalostus editable layers
     * @param conn
     * @return jätteenjalostus editable layers
     * @throws SQLException
     */
    private List<OskariLayer> getJatteenjalostusMapLayers(Connection conn) throws SQLException, ActionException {

        final String sql = "SELECT type, url, name " +
                "FROM oskari_maplayer " +
                "WHERE name='Ammassuo:AM_JAE_asfaltti' OR " +
                "name='Ammassuo:AM_JAE_betoni' OR " +
                "name='Ammassuo:AM_JAE_ekomo' OR " +
                "name='Ammassuo:AM_JAE_jatevoimalan_lentotuhka' OR " +
                "name='Ammassuo:AM_JAE_kasittelyalue' OR " +
                "name='Ammassuo:AM_JAE_katujenpuhdistusjate' OR " +
                "name='Ammassuo:AM_JAE_kuona' OR " +
                "name='Ammassuo:AM_JAE_kuona_mineraali' OR " +
                "name='Ammassuo:AM_JAE_lasi' OR " +
                "name='Ammassuo:AM_JAE_louhe' OR " +
                "name='Ammassuo:AM_JAE_maa_ja_jate' OR " +
                "name='Ammassuo:AM_JAE_maa_ja_kiviaines' OR " +
                "name='Ammassuo:AM_JAE_metalli' OR " +
                "name='Ammassuo:AM_JAE_paalit' OR " +
                "name='Ammassuo:AM_JAE_painekyllastetty_puu' OR " +
                "name='Ammassuo:AM_JAE_puu_pinnoitettu' OR " +
                "name='Ammassuo:AM_JAE_puuhake_pinnoitettu' OR " +
                "name='Ammassuo:AM_JAE_rejekti' OR " +
                "name='Ammassuo:AM_JAE_sekajate' OR " +
                "name='Ammassuo:AM_JAE_siirtokuormaus' OR " +
                "name='Ammassuo:AM_JAE_sortin_jatteet' OR " +
                "name='Ammassuo:AM_JAE_vastaanottopaikat' OR " +
                "name='Ammassuo:AM_JAE_pilaantuneet_maat' OR " +
                "name='Ammassuo:AM_JAE_pima_ja_jate' OR " +
                "name='Ammassuo:AM_JAE_puu_puhdas' OR " +
                "name='Ammassuo:AM_JAE_puuhake_puhdas' OR " +
                "name='Ammassuo:AM_JAE_risuhake' OR " +
                "name='Ammassuo:AM_JAE_risut' OR " +
                "name='Ammassuo:AM_JAE_kannot' OR " +
                "name='Ammassuo:AM_JAE_kantohake';";
        List<OskariLayer> list = LayerHelper.getMapLayers(conn, sql);
        if(list.size() != 30) {
            throw new ActionException("Cannot get all jätteenjalostus editable layers");
        }
        return list;
    }

    /**
     * Gets biojäte editable layers
     * @param conn
     * @return biojäte editable layers
     * @throws SQLException
     */
    private List<OskariLayer> getBiojateMapLayers(Connection conn) throws SQLException, ActionException {

        final String sql = "SELECT type, url, name " +
            "FROM oskari_maplayer " +
                "WHERE name='Ammassuo:AM_JAE_biokomposti' OR " +
                "name='Ammassuo:AM_JAE_hiekka' OR " +
                "name='Ammassuo:AM_JAE_kannot' OR " +
                "name='Ammassuo:AM_JAE_kantohake' OR " +
                "name='Ammassuo:AM_JAE_kasittelyalue' OR " +
                "name='Ammassuo:AM_JAE_kompostiylite' OR " +
                "name='Ammassuo:AM_JAE_lehtirisu' OR " +
                "name='Ammassuo:AM_JAE_lehtirisumurske' OR " +
                "name='Ammassuo:AM_JAE_lietekomposti' OR " +
                "name='Ammassuo:AM_JAE_maapitoiset_risut' OR " +
                "name='Ammassuo:AM_JAE_multa' OR " +
                "name='Ammassuo:AM_JAE_puu_puhdas' OR " +
                "name='Ammassuo:AM_JAE_puuhake_puhdas' OR " +
                "name='Ammassuo:AM_JAE_puutarhajate' OR " +
                "name='Ammassuo:AM_JAE_rejekti' OR " +
                "name='Ammassuo:AM_JAE_risuhake' OR " +
                "name='Ammassuo:AM_JAE_risut' OR " +
                "name='Ammassuo:AM_JAE_turve' OR " +
                "name='Ammassuo:AM_JAE_turvehiekka' OR " +
                "name='Ammassuo:AM_JAE_vastaanottopaikat' OR " +
                "name='Ammassuo:AM_JAE_viherkomposti'; ";
        List<OskariLayer> list = LayerHelper.getMapLayers(conn, sql);
        if(list.size() != 21) {
            throw new ActionException("Cannot get all biojäte editable layers");
        }
        return list;
    }
}
