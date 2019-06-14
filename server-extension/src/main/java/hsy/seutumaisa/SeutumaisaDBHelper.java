package hsy.seutumaisa;

import fi.nls.oskari.log.LogFactory;
import fi.nls.oskari.log.Logger;
import fi.nls.oskari.util.PropertyUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SeutumaisaDBHelper {
    private static final String PROPERTY_DB_URL = "db.seutumaisa.url";
    private static final String PROPERTY_DB_USER = "db.seutumaisa.username";
    private static final String PROPERTY_DB_PASSWORD = "db.seutumaisa.password";
    private static Logger LOG = LogFactory.getLogger(SeutumaisaDBHelper.class);


    private static final Connection getConnection() throws SQLException {
        String url = PropertyUtil.get(PROPERTY_DB_URL);
        String user = PropertyUtil.get(PROPERTY_DB_USER);
        String password = PropertyUtil.get(PROPERTY_DB_PASSWORD);
        Connection connection = DriverManager.getConnection(url, user, password);
        return connection;
    }

    public static JSONArray getSearchFields() throws JSONException {
        JSONArray searchFields = new JSONArray();
        searchFields.put(getMaamassaSelect().toJSON());
        searchFields.put(getMaamassaRyhmaSelect().toJSON());
        searchFields.put(getKelpoisuusluokkaSelect().toJSON());
        searchFields.put(getKohdetyyppiSelect().toJSON());
        searchFields.put(getMaamassantilaSelect().toJSON());
        searchFields.put(getSuunnitteluAikatauluRange().toJSON());
        searchFields.put(getMassanmaaraRange().toJSON());
        searchFields.put(getOmistajaSelect().toJSON());
        searchFields.put(getKuntaSelect().toJSON());

        return searchFields;
    }

    private static Select getKuntaSelect() {
        return getSelect("Kunta", "SELECT namefin FROM kuntarajat;", "kuntarajat");
    }

    private static Select getOmistajaSelect() {
        return getSelect("Omistaja (massan)", "SELECT h.organisaatio, h.id FROM maamassakohde mk LEFT JOIN henkilo h ON h.id = mk.omistaja_id WHERE organisaatio IS NOT NULL GROUP BY organisaatio, h.id;", "organisaatio", "id");
    }

    private static RangeSlider getMassanmaaraRange() {
        RangeSlider range = new RangeSlider();
        range.setId("maara");
        range.setTitle("Massan määrä");
        range.setMin(100);
        range.setMax(getMaxMaara());

        return range;
    }

    private static DateRangeSelect getSuunnitteluAikatauluRange() {
        DateRangeSelect range = new DateRangeSelect();
        range.setId("planned_date");
        range.setTitle("Suunnitteluaikataulu");
        return range;
    }

    private static Select getMaamassantilaSelect() {
        return getSelect("Maamassan tila", "SELECT maamassatila FROM maamassatieto WHERE maamassatila IS NOT NULL GROUP BY maamassatila;", "maamassatila");
    }

    private static Select getKohdetyyppiSelect() {
        return getSelect("Kohdetyyppi", "SELECT kohdetyyppi FROM maamassakohde WHERE kohdetyyppi IS NOT NULL GROUP BY kohdetyyppi;", "kohdetyyppi");
    }

    private static Select getKelpoisuusluokkaSelect() {
        return getSelect("Kelpoisuusluokka", "SELECT kelpoisuusluokka FROM maamassatieto WHERE kelpoisuusluokka IS NOT NULL GROUP BY kelpoisuusluokka;", "kelpoisuusluokka");
    }

    private static Select getMaamassaRyhmaSelect() {
        return getSelect("Massan ryhmä", "SELECT maamassaryhma FROM maamassatieto WHERE maamassaryhma IS NOT NULL GROUP BY maamassaryhma;", "maamassaryhma");
    }

    private static Select getMaamassaSelect() {
        return getSelect("Massan laji", "SELECT maamassalaji FROM maamassatieto WHERE maamassalaji IS NOT NULL GROUP BY maamassalaji;", "maamassalaji");
    }

    private static Select getSelect(String title, String sql, String column) {
        Select select = new Select();
        select.setTitle(title);
        select.setId(column);
        select.setValues(getSelectValues(sql, column));
        return select;
    }

    private static Select getSelect(String title, String sql, String column, String idColumn) {
        Select select = new Select();
        select.setTitle(title);
        select.setId(column);
        select.setValues(getSelectValues(sql, column, idColumn));
        return select;
    }

    private static int getMaxMaara() {
        int maara = 0;

        Connection conn = null;

        try  {
            conn = getConnection();

            PreparedStatement preparedStatement = conn.prepareStatement("SELECT min(maara), max(maara) FROM maamassatieto WHERE maara IS NOT NULL;");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                maara = resultSet.getInt("max");
            }

        } catch (SQLException e) {
            LOG.error(e, "Cannot create SQL query");
        } finally {
            if(conn != null) {
                try {
                    conn.close();
                } catch (Exception e) {
                    LOG.error(e);
                }
            }
        }

        return maara;
    }

    private static List<SelectValue> getSelectValues(String sql, String column) {
        List<SelectValue> values = new ArrayList<>();

        Connection conn = null;

        try  {
            conn = getConnection();


            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                SelectValue selectValue = new SelectValue();
                String value = resultSet.getString(column);
                selectValue.setId(value);
                selectValue.setTitle(value);
                values.add(selectValue);
            }

        } catch (SQLException e) {
            LOG.error(e, "Cannot create SQL query");
        } finally {
            if(conn != null) {
                try {
                    conn.close();
                } catch (Exception e) {
                    LOG.error(e);
                }
            }
        }

        return values;
    }

    private static List<SelectValue> getSelectValues(String sql, String column, String idColumn) {
        List<SelectValue> values = new ArrayList<>();

        Connection conn = null;

        try  {
            conn = getConnection();


            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                SelectValue selectValue = new SelectValue();
                selectValue.setId(resultSet.getString(idColumn));
                selectValue.setTitle(resultSet.getString(column));
                values.add(selectValue);
            }

        } catch (SQLException e) {
            LOG.error(e, "Cannot create SQL query");
        } finally {
            if(conn != null) {
                try {
                    conn.close();
                } catch (Exception e) {
                    LOG.error(e);
                }
            }
        }

        return values;
    }


}
