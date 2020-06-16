package helpers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LayerJSONHelper {

    public static JSONObject getLocale(final String fi, final String en, final String sv) throws JSONException {
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

    public static JSONObject getAttributesJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("forceProxy", true);
        json.put("geometry", "GEOM");
        return json;
    }

    public static JSONObject getForceProxyAttributeJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("forceProxy", true);
        return json;
    }

    public static JSONObject getRolePermissionsJSON() throws JSONException {
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

    public static JSONObject getCroppingLayersAttributesJSON(final String uniqueColumn) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("forceProxy", true);
        json.put("cropping", true);
        json.put("geometryColumn", "STRING");
        json.put("unique", uniqueColumn);
        json.put("geometry", "GEOM");

        return json;
    }
}
