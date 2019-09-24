package flyway.seutumaisa;

import fi.mml.map.mapwindow.service.db.OskariMapLayerGroupService;
import fi.mml.map.mapwindow.service.db.OskariMapLayerGroupServiceIbatisImpl;
import fi.nls.oskari.domain.map.DataProvider;
import fi.nls.oskari.domain.map.MaplayerGroup;
import fi.nls.oskari.domain.map.OskariLayer;
import fi.nls.oskari.log.LogFactory;
import fi.nls.oskari.log.Logger;
import fi.nls.oskari.map.layer.DataProviderService;
import fi.nls.oskari.map.layer.DataProviderServiceMybatisImpl;
import helpers.LayerHelper;
import org.flywaydb.core.api.migration.jdbc.JdbcMigration;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class V1_03_9__add_layers implements JdbcMigration {
    private static final Logger LOG = LogFactory.getLogger(V1_03_9__add_layers.class);
    private static final OskariMapLayerGroupService MAP_LAYER_GROUP_SERVICE = new OskariMapLayerGroupServiceIbatisImpl();
    private static final DataProviderService DATA_PROVIDER_SERVICE = new DataProviderServiceMybatisImpl();
    private static String SRS_3879 = "EPSG:3879";

    private static final String HKI_AVOINDATA_URL ="https://kartta.hel.fi/ws/geoserver/avoindata/wms";
    private static final String HKI_RAJATTU_URL = "https://kartta.hel.fi/ws/geoserver/helsinki/wms";
    private static final String ESPOO_AVOIN_TEKLA_URL = "https://kartat.espoo.fi/teklaogcweb/wms.ashx";
    private static final String VANTAA_AVOIN_URL = "https://gis.vantaa.fi/geoserver/wms";
    private static final String VANTAA_RAJATTU_URL = "http://gis.vantaa.fi/geo-web-services.mapdef";

    public void migrate(Connection connection) throws Exception {
        addDataproviders();

        int parentId = addMainGroup();
        boolean kantakartat = addKantakartta(parentId);
        if(!kantakartat) {
            LOG.warn("Cannot add all kantakartat layers");
        }

        boolean ajantasaAsemakaavat = addAjantasaAsemakaava(parentId);
        if(!ajantasaAsemakaavat) {
            LOG.warn("Cannot add all ajantasa-asemakaavat layers");
        }

        boolean yleiskaavat = addYleiskaavat(parentId);
        if(!yleiskaavat) {
            LOG.warn("Cannot add all yleiskaavat layers");
        }

        boolean maaperakartat = addMaaperaKartat(parentId);
        if(!maaperakartat) {
            LOG.warn("Cannot add all maaperakartat layers");
        }

    }

    private void addDataproviders()  throws JSONException{
        DataProvider dpHelsinki = DATA_PROVIDER_SERVICE.findByName("Helsingin kaupunki");
        if(dpHelsinki == null) {
            dpHelsinki = new DataProvider();
            dpHelsinki.setLocale(getLocale("Helsingin kaupunki", "Helsingin kaupunki", "Helsingin kaupunki"));
            DATA_PROVIDER_SERVICE.insert(dpHelsinki);
        }

        DataProvider dpEspoo = DATA_PROVIDER_SERVICE.findByName("Espoon kaupunki");
        if(dpEspoo == null) {
            dpEspoo = new DataProvider();
            dpEspoo.setLocale(getLocale("Espoon kaupunki", "Espoon kaupunki", "Espoon kaupunki"));
            DATA_PROVIDER_SERVICE.insert(dpEspoo);
        }

        DataProvider dpVantaa = DATA_PROVIDER_SERVICE.findByName("Vantaan kaupunki");
        if(dpVantaa == null) {
            dpVantaa = new DataProvider();
            dpVantaa.setLocale(getLocale("Vantaan kaupunki", "Vantaan kaupunki", "Vantaan kaupunki"));
            DATA_PROVIDER_SERVICE.insert(dpVantaa);
        }

    }

    private boolean addKantakartta(final int parentId) throws Exception {
        int groupId = addSubGroup(parentId, getLocale("Kantakartta", "Kantakartta", "Kantakartta"), 1);

        return addKantakarttaLayers(groupId);
    }

    private boolean addAjantasaAsemakaava(final int parentId) throws Exception {
        int groupId = addSubGroup(parentId, getLocale("Ajantasa-asemakaava","Ajantasa-asemakaava","Ajantasa-asemakaava"), 2);

        return addAjantasaAsemakaavaLayers(groupId);
    }

    private boolean addYleiskaavat(final int parentId) throws Exception {
        int groupId = addSubGroup(parentId, getLocale("Yleiskaavat","Yleiskaavat","Yleiskaavat"), 3);

        return addYleiskaavatLayers(groupId);

    }

    private boolean addMaaperaKartat(final int parentId) throws Exception {
        int groupId = addSubGroup(parentId, getLocale("Maaperäkartat","Maaperäkartat","Maaperäkartat"), 4);

        return addMaaperakartatLayers(groupId);
    }

    private int addMainGroup() throws JSONException {
        MaplayerGroup seutumaisaGroup = new MaplayerGroup();

        seutumaisaGroup.setLocale(getLocale("SeutuMaisa", "SeutuMaisa", "SeutuMaisa"));
        seutumaisaGroup.setSelectable(true);
        seutumaisaGroup.setParentId(-1);

        return MAP_LAYER_GROUP_SERVICE.insert(seutumaisaGroup);
    }

    private int addSubGroup(final int parentId, final JSONObject locale, final int order) {
        MaplayerGroup subGroup = new MaplayerGroup();
        subGroup.setLocale(locale);
        subGroup.setSelectable(true);
        subGroup.setParentId(parentId);
        subGroup.setOrderNumber(order);

        return MAP_LAYER_GROUP_SERVICE.insert(subGroup);
    }

    private List<MaplayerGroup> getLayerGroups(final int groupId) {
        List<MaplayerGroup> groups = new ArrayList<>();
        MaplayerGroup group = MAP_LAYER_GROUP_SERVICE.find(groupId);
        if(group != null) {
            groups.add(group);
        }
        return groups;
    }

    private JSONObject getLocale(final String fi, final String en, final String sv) throws JSONException {
        JSONObject json = new JSONObject();
        JSONObject fiJSON = new JSONObject();
        fiJSON.put("name", fi);
        JSONObject enJSON = new JSONObject();
        enJSON.put("name", en);
        JSONObject svJSON = new JSONObject();
        svJSON.put("name", sv);
        json.put("fi", fiJSON);
        json.put("en", enJSON);
        json.put("sv", svJSON);
        return json;
    }

    private JSONObject getAttributesJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("forceProxy", true);
        return json;
    }

    private JSONObject getRolePermissionsJSON() throws JSONException {
        JSONObject json = new JSONObject();

        JSONArray adminRights = new JSONArray();
        adminRights.put("PUBLISH");
        adminRights.put("VIEW_LAYER");
        adminRights.put("VIEW_PUBLISHED");
        json.put("Admin", adminRights);

        JSONArray userRights = new JSONArray();
        userRights.put("PUBLISH");
        userRights.put("VIEW_LAYER");
        userRights.put("VIEW_PUBLISHED");
        json.put("User", userRights);

        JSONArray guestRights = new JSONArray();
        guestRights.put("VIEW_LAYER");
        guestRights.put("VIEW_PUBLISHED");
        json.put("Guest", guestRights);

        return json;
    }

    private boolean addKantakarttaLayers(final int groupId) throws JSONException {
        JSONArray layers = new JSONArray();

        // HKI kantakartta värillinen
        layers.put(LayerHelper.generateLayerJSON(OskariLayer.TYPE_WMS, HKI_AVOINDATA_URL,
                "avoindata:Kantakartta_varillinen", "Helsingin kaupunki",
                getLocale("Helsingin kantakartta", "Helsingin kantakartta", "Helsingin kantakartta"), false,
                -1,null, -1.0,-1.0, null, null, null, null, null,
                null, false, 0, SRS_3879, LayerHelper.VERSION_WMS111, null, null,
                null, null, getRolePermissionsJSON(), getAttributesJSON()));

        // Espoon osoitekartta
        layers.put(LayerHelper.generateLayerJSON(OskariLayer.TYPE_WMS, ESPOO_AVOIN_TEKLA_URL,
                "Osoitekartta", "Espoon kaupunki",
                getLocale("Espoon osoitekartta", "Espoon osoitekartta", "Espoon osoitekartta"), false,
                -1,null, -1.0,-1.0, null, null, null, null, null,
                null, false, 0, SRS_3879, LayerHelper.VERSION_WMS111, null, null,
                null, null, getRolePermissionsJSON(), getAttributesJSON()));

        // Vantaan asemakaavan pohjakartta
        layers.put(LayerHelper.generateLayerJSON(OskariLayer.TYPE_WMS, VANTAA_AVOIN_URL,
                "gis:kantakartta", "Vantaan kaupunki",
                getLocale("Vantaan asemakaavan pohjakartta", "Vantaan asemakaavan pohjakartta", "Vantaan asemakaavan pohjakartta"), false,
                -1,null, -1.0,-1.0, null, null, null, null, null,
                null, false, 0, SRS_3879, LayerHelper.VERSION_WMS111, null, null,
                null, null, getRolePermissionsJSON(), getAttributesJSON()));

        int addedCount = LayerHelper.addLayers(layers, getLayerGroups(groupId), true);
        return layers.length() == addedCount;
    }

    private boolean addAjantasaAsemakaavaLayers(final int groupId) throws JSONException {
        JSONArray layers = new JSONArray();

        // HKI ajantasa-asemakaava värillisenä
        layers.put(LayerHelper.generateLayerJSON(OskariLayer.TYPE_WMS, HKI_AVOINDATA_URL,
                "avoindata:Ajantasa_asemakaava_maanpaallinen_varillinen", "Helsingin kaupunki",
                getLocale("Helsingin ajantasa-asemakaava", "Helsingin ajantasa-asemakaava", "Helsingin ajantasa-asemakaava"), false,
                -1,null, 12000.0,1.0, null, null, null, null, null,
                null, false, 0, SRS_3879, LayerHelper.VERSION_WMS111, null, null,
                null, null, getRolePermissionsJSON(), getAttributesJSON()));

        // Espoon ajantasa-asemakaava värillisenä
        layers.put(LayerHelper.generateLayerJSON(OskariLayer.TYPE_WMS, ESPOO_AVOIN_TEKLA_URL,
                "Ajantasa_asemakaava_vektori", "Espoon kaupunki",
                getLocale("Espoon ajantasa-asemakaava", "Espoon ajantasa-asemakaava", "Espoon ajantasa-asemakaava"), false,
                -1,null, 12000.0,1.0, null, null, null, null, null,
                null, false, 0, SRS_3879, LayerHelper.VERSION_WMS111, null, null,
                null, null, getRolePermissionsJSON(), getAttributesJSON()));

        // Vantaan ajantasa-asemakaava värillisenä
        layers.put(LayerHelper.generateLayerJSON(OskariLayer.TYPE_WMS, VANTAA_AVOIN_URL,
                "kaava:asemakaava_mv", "Vantaan kaupunki",
                getLocale("Vantaan ajantasa-asemakaava", "Vantaan ajantasa-asemakaava", "Vantaan ajantasa-asemakaava"), false,
                -1,null, 12000.0,1.0, null, null, null, null, null,
                null, false, 0, SRS_3879, LayerHelper.VERSION_WMS111, null, null,
                null, null, getRolePermissionsJSON(), getAttributesJSON()));

        int addedCount = LayerHelper.addLayers(layers, getLayerGroups(groupId), true);
        return layers.length() == addedCount;
    }

    private boolean addYleiskaavatLayers(final int groupId) throws JSONException {
        JSONArray layers = new JSONArray();

        // HKI ajantasa-asemakaava värillisenä
        layers.put(LayerHelper.generateLayerJSON(OskariLayer.TYPE_WMS, HKI_AVOINDATA_URL,
                "avoindata:Yleiskaava_2016", "Helsingin kaupunki",
                getLocale("Helsingin yleiskaava 2016", "Helsingin yleiskaava 2016", "Helsingin yleiskaava 2016"), false,
                -1,null, 12000.0,1.0, null, null, null, null, null,
                null, false, 0, SRS_3879, LayerHelper.VERSION_WMS111, null, null,
                null, null, getRolePermissionsJSON(), getAttributesJSON()));

        // Espoon ajantasa-asemakaava värillisenä
        layers.put(LayerHelper.generateLayerJSON(OskariLayer.TYPE_WMS, ESPOO_AVOIN_TEKLA_URL,
                "Yleis_ja_osayleiskaavojen_yhdistelma", "Espoon kaupunki",
                getLocale("Espoon yleis ja osayleiskaavojen yhdistelmä", "Espoon  yleis ja osayleiskaavojen yhdistelmä", "Espoon  yleis ja osayleiskaavojen yhdistelmä"), false,
                -1,null, 12000.0,1.0, null, null, null, null, null,
                null, false, 0, SRS_3879, LayerHelper.VERSION_WMS111, "WM4_SeutuMaisa", "56F_21e172fd44fbbb149713",
                null, null, getRolePermissionsJSON(), getAttributesJSON()));

        // Vantaan ajantasa-asemakaava värillisenä
        layers.put(LayerHelper.generateLayerJSON(OskariLayer.TYPE_WMS, VANTAA_AVOIN_URL,
                "kaava:yleiskaava", "Vantaan kaupunki",
                getLocale("Vantaan yleiskaava", "Vantaan yleiskaava", "Vantaan yleiskaava"), false,
                -1,null, 12000.0,1.0, null, null, null, null, null,
                null, false, 0, SRS_3879, LayerHelper.VERSION_WMS111, null, null,
                null, null, getRolePermissionsJSON(), getAttributesJSON()));

        int addedCount = LayerHelper.addLayers(layers, getLayerGroups(groupId), true);
        return layers.length() == addedCount;
    }

    private boolean addMaaperakartatLayers(final int groupId) throws JSONException {
        JSONArray layers = new JSONArray();

        // HKI maaperäkartta rasteri
        layers.put(LayerHelper.generateLayerJSON(OskariLayer.TYPE_WMS, HKI_RAJATTU_URL,
                "Maaperakartta_rasteri", "Helsingin kaupunki",
                getLocale("Helsingin maaperäkartta", "Helsingin maaperäkartta", "Helsingin maaperäkartta"), false,
                -1,null, 12000.0,1.0, null, null, null, null, null,
                null, false, 0, SRS_3879, LayerHelper.VERSION_WMS111, "HSY", "8ab34c2dec",
                null, null, getRolePermissionsJSON(), getAttributesJSON()));

        // Espoon maaperäkartta
        layers.put(LayerHelper.generateLayerJSON(OskariLayer.TYPE_WMS, ESPOO_AVOIN_TEKLA_URL,
                "Espoon_maaperakartta_10000", "Espoon kaupunki",
                getLocale("Espoon maaperakartta 1:10 000", "Espoon maaperakartta 1:10 000", "Espoon maaperakartta 1:10 000"), false,
                -1,null, 12000.0,1.0, null, null, null, null, null,
                null, false, 0, SRS_3879, LayerHelper.VERSION_WMS111, "WM4_SeutuMaisa", "56F_21e172fd44fbbb149713",
                null, null, getRolePermissionsJSON(), getAttributesJSON()));

        // Vantaan Maalajikartta
        layers.put(LayerHelper.generateLayerJSON(OskariLayer.TYPE_WMS, VANTAA_RAJATTU_URL,
                "Maalajikartta", "Vantaan kaupunki",
                getLocale("Vantaan maalajikartta", "Vantaan maalajikartta", "Vantaan maalajikartta"), false,
                -1,null, 12000.0,1.0, null, null, null, null, null,
                null, false, 0, SRS_3879, LayerHelper.VERSION_WMS111, null, null,
                null, null, getRolePermissionsJSON(), getAttributesJSON()));

        int addedCount = LayerHelper.addLayers(layers, getLayerGroups(groupId), true);
        return layers.length() == addedCount;
    }

}


