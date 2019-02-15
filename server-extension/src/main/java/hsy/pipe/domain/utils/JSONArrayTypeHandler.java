package hsy.pipe.domain.utils;

import java.sql.SQLException;
import java.sql.Types;

import org.json.JSONArray;
import org.json.JSONException;

import com.ibatis.sqlmap.client.extensions.ParameterSetter;
import com.ibatis.sqlmap.client.extensions.ResultGetter;
import com.ibatis.sqlmap.client.extensions.TypeHandlerCallback;

import fi.nls.oskari.log.LogFactory;
import fi.nls.oskari.log.Logger;

public class JSONArrayTypeHandler implements TypeHandlerCallback {
	private final static Logger log = LogFactory.getLogger(JSONArrayTypeHandler.class);
	
	public void setParameter(ParameterSetter parameterSetter, Object parameter) throws SQLException {
        if (parameter == null) {
            parameterSetter.setNull(Types.VARCHAR);
        } else {
            parameterSetter.setString(((JSONArray)parameter).toString());
        }
    }

    public Object getResult(ResultGetter resultGetter) throws SQLException {
        String value = resultGetter.getString();
        if (resultGetter.wasNull()) {
            return null;
        }
        return valueOf(value);
    }

    public Object valueOf(String s) {
        JSONArray jsonArray = null;
        try {
        	jsonArray = new JSONArray(s);
        } catch (JSONException e) {
            log.error("Couldn't parse DB string to JSONArray:", s, e);
            return new JSONArray();
        }
        return jsonArray;
    }
}
