package flyway.ammassuo;

import fi.mml.map.mapwindow.service.db.OskariMapLayerGroupService;
import fi.mml.map.mapwindow.service.db.OskariMapLayerGroupServiceIbatisImpl;
import fi.nls.oskari.domain.map.DataProvider;
import fi.nls.oskari.domain.map.MaplayerGroup;
import fi.nls.oskari.domain.map.OskariLayer;
import fi.nls.oskari.log.LogFactory;
import fi.nls.oskari.log.Logger;
import fi.nls.oskari.map.layer.DataProviderService;
import fi.nls.oskari.map.layer.DataProviderServiceMybatisImpl;
import fi.nls.oskari.util.PropertyUtil;
import helpers.LayerHelper;
import helpers.LayerJSONHelper;
import org.flywaydb.core.api.migration.jdbc.JdbcMigration;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import fi.nls.oskari.util.IOHelper;
import java.io.IOException;
import java.sql.Connection;

public class V1_02_0__add_sijoituspaikat_maplayers implements JdbcMigration {
    private static final Logger LOG = LogFactory.getLogger(V1_02_0__add_sijoituspaikat_maplayers.class);
    private static final OskariMapLayerGroupService MAP_LAYER_GROUP_SERVICE = new OskariMapLayerGroupServiceIbatisImpl();
    private static final DataProviderService DATA_PROVIDER_SERVICE = new DataProviderServiceMybatisImpl();
    private static final String SRS_3879 = "EPSG:3879";

    private static String SIJOITUSPAIKAT_DATA_URL  = PropertyUtil.get("geoserver.url", "http://localhost:8080/geoserver") + "/wfs";

    public void migrate(Connection connection) throws Exception {
        addMainGroup();
        addDataproviders();

        int parentId = -1;

        boolean sijoituspaikat = addSijoituspaikatKartat(parentId, connection);
        if (!sijoituspaikat) {
            LOG.warn("Cannot add all sijoituspaikat layers");
        }
    }

    private void addDataproviders() throws JSONException {
        DataProvider dpEspoo = DATA_PROVIDER_SERVICE.findByName("Espoon kaupunki");
        if (dpEspoo == null) {
            dpEspoo = new DataProvider();
            dpEspoo.setLocale(LayerJSONHelper.getLocale("Espoon kaupunki", "Espoon kaupunki", "Espoon kaupunki"));
            DATA_PROVIDER_SERVICE.insert(dpEspoo);
        }

    }

    private boolean addSijoituspaikatKartat(final int parentId, final Connection conn) throws Exception {
        int groupId = addSubGroup(parentId, LayerJSONHelper.getLocale("Sijoituspaikat", "Sijoituspaikat", "Sijoituspaikat"), 16);

        return addSijoituspaikatLayers(groupId, conn);
    }

    private int addMainGroup() throws JSONException {
        MaplayerGroup sijoituspaikatGroup = new MaplayerGroup();

        sijoituspaikatGroup.setLocale(LayerJSONHelper.getLocale("Sijoituspaikat", "Sijoituspaikat", "Sijoituspaikat"));
        sijoituspaikatGroup.setSelectable(true);
        sijoituspaikatGroup.setParentId(-1);
        try {
            MaplayerGroup group = MAP_LAYER_GROUP_SERVICE.findByName(sijoituspaikatGroup.getLocale().getString("fi"));
            if (group != null) {
                return group.getId();
            }
        } catch (JSONException jex) {
            LOG.error(jex, "Error getting maplayer group");
        }
        return MAP_LAYER_GROUP_SERVICE.insert(sijoituspaikatGroup);
    }

    private int addSubGroup(final int parentId, final JSONObject locale, final int order) {
        MaplayerGroup subGroup = new MaplayerGroup();
        subGroup.setLocale(locale);
        subGroup.setSelectable(true);
        subGroup.setParentId(parentId);
        subGroup.setOrderNumber(order);

        try {
            MaplayerGroup group = MAP_LAYER_GROUP_SERVICE.findByName(locale.getString("fi"));
            if (group != null) {
                return group.getId();
            }
        } catch (JSONException jex) {
            LOG.error(jex, "Error getting maplayer group");
        }

        return MAP_LAYER_GROUP_SERVICE.insert(subGroup);
    }

    private boolean addSijoituspaikatLayers(final int groupId, final Connection conn) throws IOException, JSONException {

        final String json = IOHelper.readString(getClass().getResourceAsStream("hsy-sijoituspaikat-layers.json"));
        JSONArray array = new JSONArray(json);
        JSONArray layers = new JSONArray();

        for(int i = 0; i < array.length(); i++) {

            JSONObject obj2 = (JSONObject)array.get(i);
            String name = obj2.get("name").toString();
            String locale = obj2.get("locale").toString();
            JSONObject style = (JSONObject)obj2.get("style");

            layers.put(LayerHelper.generateLayerJSON(OskariLayer.TYPE_WFS, SIJOITUSPAIKAT_DATA_URL, name, "Espoon kaupunki",
                LayerJSONHelper.getLocale(locale,locale,locale), false, -1,
                locale, 1500000.0, 1.0, null, null, null, null, null, null, false, 0, SRS_3879, LayerHelper.VERSION_WFS110,
                null, null, null, style, LayerJSONHelper.getRolePermissionsAmmassuoJSON("Ammassuo_paakayttaja"),null));

        }

        int addedCount = LayerHelper.addLayers(layers, LayerHelper.getLayerGroups(groupId), true, conn);
        return layers.length() == addedCount;
    }

}
