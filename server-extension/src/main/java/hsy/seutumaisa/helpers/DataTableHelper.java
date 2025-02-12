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
        columns.add(new SearchParams("Kohteen id"));
        columns.add(new SearchParams("Kohteen tunnus"));
        columns.add(new SearchParams("Massan laji"));
        columns.add(new SearchParams("Massan ryhmä"));
        columns.add(new SearchParams("Kelpoisuusluokka"));
        columns.add(new SearchParams("Pilaantuneisuus"));
        columns.add(new SearchParams("Kohdetyyppi"));
        columns.add(new SearchParams("Maamassan tila"));
        columns.add(new SearchParams("Suunniteltu aikataulu (alku)"));
        columns.add(new SearchParams("Suunniteltu aikataulu (loppu)"));
        columns.add(new SearchParams("Massan määrä"));
        columns.add(new SearchParams("Omistajan nimi (massan)"));
        columns.add(new SearchParams("Omistajan sähköposti"));
        columns.add(new SearchParams("Omistajan puhelinnumero"));
        columns.add(new SearchParams("Omistajan organisaatio"));
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
     * Gets columns definitions.
     * @return
     * @throws JSONException
     */
    public static JSONArray getColumnDefs() throws JSONException {
        JSONArray columnDefs = new JSONArray();
        JSONObject defs = new JSONObject();
        JSONArray targets = new JSONArray();
        // geometry column index to hide
        int geomIndex = columns.size() - 1;
        targets.put(geomIndex);
        defs.put("targets", targets);
        defs.put("visible", false);
        defs.put("searchable", false);
        columnDefs.put(defs);

        return columnDefs;
    }


}
