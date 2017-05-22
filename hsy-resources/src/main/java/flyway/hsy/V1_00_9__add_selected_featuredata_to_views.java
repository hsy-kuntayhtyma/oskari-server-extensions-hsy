package flyway.hsy;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.flywaydb.core.api.migration.jdbc.JdbcMigration;

import fi.nls.oskari.map.view.ViewService;
import fi.nls.oskari.map.view.ViewServiceIbatisImpl;

public class V1_00_9__add_selected_featuredata_to_views implements JdbcMigration {
	
	private static final ViewService VIEW_SERVICE = new ViewServiceIbatisImpl();
	private static final  String SELECTED_FEATUREDATA = "selected-featuredata";
	
	public void migrate(Connection connection) throws Exception {
		long viewId = VIEW_SERVICE.getDefaultViewId();
		makeInsert(viewId,connection);
	}
	
	private void makeInsert(long viewId, Connection connection)
            throws Exception {

        final PreparedStatement statement =
                connection.prepareStatement("INSERT INTO portti_view_bundle_seq" +
                        "(view_id, bundle_id, seqno, config, state, startup, bundleinstance) " +
                        "VALUES (" +
                        "?, " +
                        "(SELECT id FROM portti_bundle WHERE name=?), " +
                        "(SELECT max(seqno)+1 FROM portti_view_bundle_seq WHERE view_id=?), " +
                        "?, ?, " +
                        "(SELECT startup FROM portti_bundle WHERE name=?), " +
                        "?)");

        statement.setLong(1, viewId);
        statement.setString(2, SELECTED_FEATUREDATA);
        statement.setLong(3, viewId);
        statement.setString(4, "{}");
        statement.setString(5, "{}");
        statement.setString(6, SELECTED_FEATUREDATA);
        statement.setString(7, SELECTED_FEATUREDATA);

        try {
            statement.execute();
        } finally {
            statement.close();
        }
    }
}
