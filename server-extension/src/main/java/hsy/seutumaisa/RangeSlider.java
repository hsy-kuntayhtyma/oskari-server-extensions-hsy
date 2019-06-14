package hsy.seutumaisa;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RangeSlider {
    private String type;
    private String title;
    private int min;
    private int max;
    private String id;

    public RangeSlider() {
        this.type = "range";
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("id", this.id);
        json.put("title", this.title);
        json.put("type", this.type);
        json.put("min", this.min);
        json.put("max", this.max);
        return json;
    }
}
