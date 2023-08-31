package flyway.hsy;

import java.sql.SQLException;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.oskari.helpers.BundleHelper;

import fi.nls.oskari.domain.map.view.Bundle;

public class V1_00_0__register_download_basket_bundle extends BaseJavaMigration {

	private static final String DOWNLOAD_BASKET = "download-basket";

	public void migrate(Context context) throws SQLException {
	// BundleHelper checks if these bundles are already registered
		Bundle downloadBasket = new Bundle();
		downloadBasket.setConfig("{}");
		downloadBasket.setState("{}");
		downloadBasket.setName(DOWNLOAD_BASKET);
		//downloadBasket.setStartup(BundleHelper.getDefaultBundleStartup(NAMESPACE, DOWNLOAD_BASKET, "Download basket"));
		BundleHelper.registerBundle(context.getConnection(), downloadBasket);
	}

}
