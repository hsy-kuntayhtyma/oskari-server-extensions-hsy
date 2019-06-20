package hsy.seutumaisa.domain;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Create Datatable results
 */
public class DataTableResult {
    private JSONArray columns;
    private JSONArray columnDefs;
    private JSONArray data;

    public JSONArray getColumns() {
        return columns;
    }

    public void setColumns(JSONArray columns) {
        this.columns = columns;
    }

    public JSONArray getColumnDefs() {
        return columnDefs;
    }

    public void setColumnDefs(JSONArray columnDefs) {
        this.columnDefs = columnDefs;
    }

    public JSONArray getData() {
        return data;
    }

    public void setData(JSONArray data) {
        this.data = data;
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        if (this.columns == null) {
            this.columns = new JSONArray();
        }
        if (this.columnDefs == null) {
            this.columnDefs = new JSONArray();
        }
        if (this.data == null) {
            this.data = new JSONArray();
        }

        json.put("columns", this.columns);
        json.put("columnDefs", this.columnDefs);
        json.put("data", this.data);

        return json;
    }
}
