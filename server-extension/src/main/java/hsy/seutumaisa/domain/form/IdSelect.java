package hsy.seutumaisa.domain.form;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Creates IdSelect select
 */
public class IdSelect {
    private String type;
    private String title;
    private String id;

    public IdSelect() {
        this.type = "id";
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
        return json;
    }
}
