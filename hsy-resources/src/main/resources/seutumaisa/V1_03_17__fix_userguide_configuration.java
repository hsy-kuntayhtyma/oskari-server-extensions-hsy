package flyway.seutumaisa;

import fi.nls.oskari.domain.map.view.Bundle;
import fi.nls.oskari.map.view.ViewService;
import fi.nls.oskari.map.view.AppSetupServiceMybatisImpl;
import org.flywaydb.core.api.migration.jdbc.JdbcMigration;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class V1_03_17__fix_userguide_configuration implements JdbcMigration {
    private static final ViewService VIEW_SERVICE = new AppSetupServiceMybatisImpl();
    private static final String ROLE_SEUTUMAISA = "seutumaisa";
    private static final String BUNDLE_USERGUIDE = "userguide";

    public void migrate(Connection connection) throws SQLException, JSONException {
        long viewId = VIEW_SERVICE.getDefaultViewIdForRole(ROLE_SEUTUMAISA);

        updateUserguideConf(connection, viewId);
    }

    private void updateUserguideConf(Connection conn, long viewId) throws SQLException, JSONException {
        final String sql = "UPDATE portti_view_bundle_seq " +
                "SET config=? " +
                "WHERE bundleinstance = ? and view_id=?";

        JSONObject conf = new JSONObject("{\"tabs\": [" +
                "  {" +
                "    \"tags\": \"ohje_karttaikkuna\"," +
                "    \"title\": \"Karttaikkuna\"" +
                "  }," +
                "  {" +
                "    \"tags\": \"ohje_tyokalut\"," +
                "    \"title\": \"Työkalut\"" +
                "  }," +
                "  {" +
                "    \"tags\": \"ohje_haku\"," +
                "    \"title\": \"Haku\"" +
                "  }," +
                "  {" +
                "    \"tags\": \"ohje_karttatasot\"," +
                "    \"title\": \"Karttatasot\"" +
                "  }" +
                "]}");


        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, conf.toString());
            statement.setString(2, "userguide");
            statement.setLong(3, viewId);
            statement.execute();
        }
    }

}
