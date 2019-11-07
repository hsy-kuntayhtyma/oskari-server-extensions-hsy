package hsy.seutumaisa.helpers;

import hsy.seutumaisa.domain.SearchParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper for datatable creation.
 */
public class DataTableHelper {
    private final static String KEY_TITLE = "title";
    private static List<SearchParams> columns = new ArrayList<>();

    /**
     * DAtatable columns and their titles
     */
    static {
        columns.add(new SearchParams("Massan laji"));
        columns.add(new SearchParams("Massan ryhmä"));
        columns.add(new SearchParams("Kelpoisuusluokka"));
        columns.add(new SearchParams("Kohdetyyppi"));
        columns.add(new SearchParams("Maamassan tila"));
        columns.add(new SearchParams("Suunnitteluaikataulu (alku)"));
        columns.add(new SearchParams("Suunnitteluaikataulu (loppu)"));
        columns.add(new SearchParams("Massan määrä (m&sup3;)"));
        columns.add(new SearchParams("Omistaja (massan)"));
        columns.add(new SearchParams("Kunta"));
        columns.add(new SearchParams("GeoJSON"));
    }

    /**
     * Gets datatable columns
     * @return
     * @throws JSONException
     */
    public static JSONArray getColumns() throws JSONException {
        JSONArray columnsJSON = new JSONArray();
        for (SearchParams column : columns) {
            JSONObject json = new JSONObject();
            json.put(KEY_TITLE, column.getTitle());
            columnsJSON.put(json);
        }
        return columnsJSON;
    }

    /**
     * Gets columns definations.
     * @param geomIndex geometry column index to hide
     * @return
     * @throws JSONException
     */
    public static JSONArray getColumnDefs(int geomIndex) throws JSONException {
        JSONArray columnDefs = new JSONArray();
        JSONObject defs = new JSONObject();
        JSONArray targets = new JSONArray();
        targets.put(geomIndex);
        defs.put("targets", targets);
        defs.put("visible", false);
        defs.put("searchable", false);
        columnDefs.put(defs);

        return columnDefs;
    }


}
