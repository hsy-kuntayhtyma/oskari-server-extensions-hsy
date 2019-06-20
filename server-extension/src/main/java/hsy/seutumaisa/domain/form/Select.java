package hsy.seutumaisa.domain.form;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Creates select
 */
public class Select {
    private String type;
    private String placeHolderText;
    private List<SelectValue> values;
    private String title;
    private String id;

    public Select() {
        this.type = "select";
        this.values = new ArrayList<>();
        this.placeHolderText = "Valitse";
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPlaceHolderText() {
        return placeHolderText;
    }

    public void setPlaceHolderText(String placeHolderText) {
        this.placeHolderText = placeHolderText;
    }

    public List<SelectValue> getValues() {
        return values;
    }

    public void addValue(SelectValue value) {
        this.values.add(value);
    }

    public void setValues(List<SelectValue> values) {
        this.values = values;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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
        json.put("placeHolderText", this.placeHolderText);
        JSONArray values = new JSONArray();
        Collections.sort(this.values, new Comparator<SelectValue>() {
            public int compare(SelectValue value1, SelectValue value2) {
                return value1.getTitle().compareTo(value2.getTitle());
            }
        });
        for(SelectValue value : this.values){
            values.put(value.toJSON());
        }
        json.put("values", values);
        return json;
    }

}
