package helpers;

import fi.mml.map.mapwindow.service.db.OskariMapLayerGroupService;
import fi.mml.map.mapwindow.service.db.OskariMapLayerGroupServiceIbatisImpl;
import fi.mml.portti.domain.permissions.Permissions;
import fi.mml.portti.service.db.permissions.PermissionsService;
import fi.mml.portti.service.db.permissions.PermissionsServiceIbatisImpl;
import fi.nls.oskari.domain.Role;
import fi.nls.oskari.domain.map.DataProvider;
import fi.nls.oskari.domain.map.MaplayerGroup;
import fi.nls.oskari.domain.map.OskariLayer;
import fi.nls.oskari.domain.map.view.Bundle;
import fi.nls.oskari.domain.map.view.View;
import fi.nls.oskari.log.LogFactory;
import fi.nls.oskari.log.Logger;
import fi.nls.oskari.map.data.domain.OskariLayerResource;
import fi.nls.oskari.map.layer.DataProviderService;
import fi.nls.oskari.map.layer.DataProviderServiceIbatisImpl;
import fi.nls.oskari.map.layer.OskariLayerService;
import fi.nls.oskari.map.layer.OskariLayerServiceIbatisImpl;
import fi.nls.oskari.map.layer.group.link.OskariLayerGroupLink;
import fi.nls.oskari.map.layer.group.link.OskariLayerGroupLinkService;
import fi.nls.oskari.map.layer.group.link.OskariLayerGroupLinkServiceMybatisImpl;
import fi.nls.oskari.map.view.ViewException;
import fi.nls.oskari.map.view.ViewService;
import fi.nls.oskari.map.view.ViewServiceIbatisImpl;
import fi.nls.oskari.map.view.util.ViewHelper;
import fi.nls.oskari.permission.domain.Permission;
import fi.nls.oskari.permission.domain.Resource;
import fi.nls.oskari.user.MybatisRoleService;
import fi.nls.oskari.util.JSONHelper;
import flyway.seutumaisa.V1_03_6__mappfull_add_selected_layers;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.oskari.service.util.ServiceFactory;

import java.sql.Connection;
import java.util.*;

/***
 * @author Marko Kuosmanen
 * This class helps for anything of layers
 */
public class LayerHelper {
    public static final String ROLE_SEUTUMAISA = "SeutuMaisa";
    public static final String ROLE_AMMASSUO = "Ammassuo";

    public static final String VERSION_WFS110 = "1.1.0";
    public static final String VERSION_WFS200 = "2.0.0";
    public static final String VERSION_WMS111 = "1.1.1";
    public static final String VERSION_WMS130 = "1.3.0";

    private static final Logger LOG = LogFactory.getLogger(LayerHelper.class);

    private static final ViewService VIEW_SERVICE = new ViewServiceIbatisImpl();
    private static final OskariMapLayerGroupService MAP_LAYER_GROUP_SERVICE = new OskariMapLayerGroupServiceIbatisImpl();
    private static final PermissionsService PERMISSIONS_SERVICE = new PermissionsServiceIbatisImpl();
    private static final MybatisRoleService ROLE_SERVICE = new MybatisRoleService();
    private static final DataProviderService DATA_PROVIDER_SERVICE = new DataProviderServiceIbatisImpl();
    private static final OskariLayerGroupLinkService LINK_SERVICE = new OskariLayerGroupLinkServiceMybatisImpl();
    private static final String BUNDLE_MAPFULL = "mapfull";
    private static final String BACKGROUND_LAYER_SELECTION_PLUGIN = "Oskari.mapframework.bundle.mapmodule.plugin.BackgroundLayerSelectionPlugin";

    /**
     * Sets background layer selection plugin layers
     *
     * @param connection sql connection
     * @param layerIds   layerids
     */
    public static void setBackgroundSelectionPluginLayers(final Connection connection, final String[] layerIds) throws ViewException, JSONException {

        if(layerIds == null || layerIds.length == 0) {
            return;
        }

        List<View> views = VIEW_SERVICE.getViewsForUser(-1);
        for (View v : views) {
            if (v.isDefault()) {
                final Bundle mapfull = v.getBundleByName(BUNDLE_MAPFULL);

                final JSONObject config = mapfull.getConfigJSON();
                final JSONArray plugins = config.optJSONArray("plugins");

                for (int i = 0; i < plugins.length(); ++i) {
                    JSONObject plugin = plugins.getJSONObject(i);
                    if (BACKGROUND_LAYER_SELECTION_PLUGIN.equals(plugin.optString("id"))) {
                        final JSONObject pluginConfig = plugin.optJSONObject("config");
                        if (pluginConfig != null) {
                            JSONArray baseLayers = new JSONArray();
                            for (String layerId : layerIds) {
                                baseLayers.put(layerId);
                            }
                            pluginConfig.remove("baseLayers");
                            pluginConfig.put("baseLayers", baseLayers);
                            break;
                        }

                    }
                }

                VIEW_SERVICE.updateBundleSettingsForView(v.getId(), mapfull);
            }
        }
    }

    /**
     * Adds layers
     * @param layerArray layers array
     * @param maplayerGroups layer groups
     * @param notCheckExitings not cehcks exiting layers
     * @return added layers length
     */
    public static int addLayers(final JSONArray layerArray, final List<MaplayerGroup> maplayerGroups, final boolean notCheckExitings) {
        List<Integer> addedLayers = new ArrayList<>();
        OskariLayerService service = new OskariLayerServiceIbatisImpl();
        try {
            for (int i = 0; i < layerArray.length(); i++) {
                JSONObject layerJSON = layerArray.getJSONObject(i);

                OskariLayer layer = parseLayer(layerJSON);
                List<OskariLayer> dbLayers = service.findByUrlAndName(layer.getUrl(), layer.getName());
                if (!dbLayers.isEmpty() && !notCheckExitings) {
                    if (dbLayers.size() > 1) {
                        LOG.warn(new Object[]{"Found multiple layers with same url and name. Using first one. Url:", layer.getUrl(), "- name:", layer.getName()});
                    }
                    continue;
                } else {
                    int id = service.insert(layer);
                    layer.setId(id);

                    setupLayerPermissions(layerJSON.getJSONObject("role_permissions"), layer);

                    // Handle layer groups
                    for(int j=0; j<maplayerGroups.size(); j++) {
                        MaplayerGroup group = maplayerGroups.get(j);
                        MaplayerGroup foundedGroup = MAP_LAYER_GROUP_SERVICE.findByName(group.getName("fi"));
                        if (foundedGroup == null) {
                            int groupId = MAP_LAYER_GROUP_SERVICE.insert(group);
                            LINK_SERVICE.insert(new OskariLayerGroupLink(id, groupId));
                        } else {
                            LINK_SERVICE.insert(new OskariLayerGroupLink(id, foundedGroup.getId()));
                        }
                    }

                    addedLayers.add(id);
                }

            }
        } catch (JSONException jsonex) {
            LOG.error(jsonex, "Cannot add layer(s)");
        }

        return addedLayers.size();

    }

    /**
     * Generate layer JSON
     * @param type use OskariLayer.TYPE_
     * @param url
     * @param name
     * @param dataprovider
     * @param locale
     * @param baseMap
     * @param opacity optional
     * @param style optional
     * @param minscale optional
     * @param maxscale optional
     * @param legendImage optional
     * @param metadataId optional
     * @param gfiType optional
     * @param gfiXslt optional
     * @param gfiContent optional
     * @param geometry optional
     * @param realtime
     * @param refreshRate optional
     * @param srsName optional
     * @param version optional, use VERSION_
     * @param username optional
     * @param password optional
     * @param params optional
     * @param options optional
     * @return layer json
     * @throws JSONException
     */
    public static JSONObject generateLayerJSON(final String type, final String url, final String name, final String dataprovider,
                                        final JSONObject locale, final Boolean baseMap, final Integer opacity, final String style,
                                        final Double minscale, final Double maxscale, final String legendImage, final String metadataId,
                                        final String gfiType, final String gfiXslt, final String gfiContent, final String geometry,
                                        final Boolean realtime, final Integer refreshRate, final String srsName, final String version,
                                        final String username, final String password, final JSONObject params, final JSONObject options,
                                        final JSONObject rolePermissions, final JSONObject attributes) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("type", type);
        json.put("url", url);

        json.put("name", name);
        json.put("dataprovider", dataprovider);
        json.put("locale", locale);
        json.put("base_map", baseMap);
        if(opacity != null && opacity != -1) {
            json.put("opacity", opacity);
        }
        if(style != null && !style.isEmpty()) {
            json.put("style", style);
        }
        if(minscale != null && !minscale.equals(-1.0)) {
            json.put("minscale", minscale);
        }
        if(maxscale != null && !maxscale.equals(-1.0)) {
            json.put("maxscale", maxscale);
        }

        if(legendImage != null && !legendImage.isEmpty()) {
            json.put("legend_image", legendImage);
        }
        if(metadataId != null && !metadataId.isEmpty()) {
            json.put("metadataid", metadataId);
        }
        if(gfiType != null && !gfiType.isEmpty()) {
            json.put("gfi_type", gfiType);
        }
        if(gfiXslt != null && !gfiXslt.isEmpty()) {
            json.put("gfi_xslt", gfiXslt);
        }
        if(gfiContent != null && !gfiContent.isEmpty()) {
            json.put("gfi_content", gfiContent);
        }
        if(geometry != null && !geometry.isEmpty()) {
            json.put("geometry", geometry);
        }

        json.put("realtime", realtime);
        if(refreshRate != null && refreshRate != -1) {
            json.put("refresh_rate", refreshRate);
        }
        if(srsName != null && !srsName.isEmpty()) {
            json.put("srs_name", srsName);
        }
        if(version != null && !version.isEmpty()) {
            json.put("version", version);
        }
        if(username != null && !username.isEmpty()) {
            json.put("username", username);
        }
        if(password != null && !password.isEmpty()) {
            json.put("password", password);
        }
        if(params != null) {
            json.put("params", params);
        }
        if(options != null) {
            json.put("options", options);
        }
        if(rolePermissions != null) {
            json.put("role_permissions", rolePermissions);
        }
        if(attributes != null) {
            json.put("attributes", attributes);
        }

        return json;
    }

    private static OskariLayer parseLayer(final JSONObject json) throws JSONException {
        OskariLayer layer = new OskariLayer();
        layer.setType(json.getString("type"));
        layer.setUrl(json.getString("url"));
        layer.setName(json.getString("name"));
        String dataproviderName = json.getString("dataprovider");
        layer.setLocale(json.getJSONObject("locale"));
        layer.setBaseMap(json.optBoolean("base_map", layer.isBaseMap()));
        layer.setOpacity(json.optInt("opacity", layer.getOpacity()));
        layer.setStyle(json.optString("style", layer.getStyle()));
        layer.setMinScale(json.optDouble("minscale", layer.getMinScale()));
        layer.setMaxScale(json.optDouble("maxscale", layer.getMaxScale()));
        layer.setLegendImage(json.optString("legend_image", layer.getLegendImage()));
        layer.setMetadataId(json.optString("metadataid", layer.getMetadataId()));
        layer.setGfiType(json.optString("gfi_type", layer.getGfiType()));
        layer.setGfiXslt(json.optString("gfi_xslt", layer.getGfiXslt()));
        layer.setGfiContent(json.optString("gfi_content", layer.getGfiContent()));
        layer.setGeometry(json.optString("geometry", layer.getGeometry()));
        layer.setRealtime(json.optBoolean("realtime", layer.getRealtime()));
        layer.setRefreshRate(json.optInt("refresh_rate", layer.getRefreshRate()));
        layer.setSrs_name(json.optString("srs_name", layer.getSrs_name()));
        layer.setVersion(json.optString("version", layer.getVersion()));
        layer.setUsername(json.optString("username", layer.getUsername()));
        layer.setPassword(json.optString("password", layer.getPassword()));
        JSONObject params = json.optJSONObject("params");
        if (params != null) {
            layer.setParams(params);
        }

        JSONObject options = json.optJSONObject("options");
        if (options != null) {
            layer.setOptions(options);
        }

        JSONObject attributes = json.optJSONObject("attributes");
        if (attributes != null) {
            layer.setAttributes(attributes);
        }

        DataProvider dataProvider = DATA_PROVIDER_SERVICE.findByName(dataproviderName);
        if (dataProvider == null) {
            LOG.warn(new Object[]{"Didn't find match for dataprovider:", dataproviderName});
        } else {
            layer.addDataprovider(dataProvider);
        }

        return layer;
    }

    private static void setupLayerPermissions(final JSONObject permissions, final OskariLayer layer) {
        if (permissions != null) {
            Resource res = new OskariLayerResource(layer);
            Iterator roleNames = permissions.keys();

            while(true) {
                while(roleNames.hasNext()) {
                    String roleName = (String)roleNames.next();
                    Role role = ROLE_SERVICE.findRoleByName(roleName);
                    if (role == null) {
                        LOG.warn(new Object[]{"Couldn't find matching role in DB:", roleName, "- Skipping!"});
                    } else {
                        JSONArray permissionTypes = permissions.optJSONArray(roleName);
                        if (permissionTypes != null) {
                            for(int i = 0; i < permissionTypes.length(); ++i) {
                                Permission permission = new Permission();
                                permission.setExternalType("ROLE");
                                permission.setExternalId("" + role.getId());
                                String type = permissionTypes.optString(i);
                                permission.setType(type);
                                res.addPermission(permission);
                            }
                        }
                    }
                }

                PERMISSIONS_SERVICE.saveResourcePermissions(res);
                return;
            }
        }
    }

    /**
     * Exists maplayer group, checks if maplayer groupt already exists.
     * @param name group name
     * @return maplayer group
     */
    public static MaplayerGroup getMapLayerGroup(final String name) {
        return MAP_LAYER_GROUP_SERVICE.findByName(name);
    }
}
