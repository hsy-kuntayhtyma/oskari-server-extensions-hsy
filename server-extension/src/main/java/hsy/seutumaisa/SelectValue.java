package hsy.seutumaisa;

import org.json.JSONException;
import org.json.JSONObject;

public class SelectValue {
    private String id;
    private String title;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("id", this.id);
        json.put("title", this.title);
        return json;
    }
}
