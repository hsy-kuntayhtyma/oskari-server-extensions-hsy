package flyway.seutumaisa;

import java.sql.Connection;

import org.flywaydb.core.api.migration.jdbc.JdbcMigration;

import fi.nls.oskari.db.BundleHelper;
import fi.nls.oskari.domain.map.view.Bundle;

public class V1_03_13__register_timeseries_bundle implements JdbcMigration{

	private static final String NAMESPACE = "seutumaisa";
	private static final String TIMESERIES = "timeseries";

	public void migrate(Connection connection) {
	// BundleHelper checks if these bundles are already registered
		Bundle timeseries = new Bundle();
		timeseries.setConfig("{}");
		timeseries.setState("{}");
		timeseries.setName(TIMESERIES);
		timeseries.setStartup(BundleHelper.getDefaultBundleStartup(NAMESPACE, TIMESERIES, "Timeseries"));
		BundleHelper.registerBundle(timeseries);
	}
}
