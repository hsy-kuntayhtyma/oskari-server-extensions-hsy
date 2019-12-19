package hsy.seutumaisa.helpers;

import fi.nls.oskari.control.ActionParameters;
import fi.nls.oskari.control.ActionParamsException;
import fi.nls.oskari.log.LogFactory;
import fi.nls.oskari.log.Logger;
import fi.nls.oskari.util.PropertyUtil;
import hsy.seutumaisa.domain.DataTableResult;
import hsy.seutumaisa.domain.Range;
import hsy.seutumaisa.domain.SearchParams;
import hsy.seutumaisa.domain.form.DateRangeSelect;
import hsy.seutumaisa.domain.form.RangeSlider;
import hsy.seutumaisa.domain.form.Select;
import hsy.seutumaisa.domain.form.SelectValue;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper for seutumasia db handling.
 */
public class SeutumaisaHistoryDBHelper {
    private static final String PROPERTY_DB_URL = "db.seutumaisa.url";
    private static final String PROPERTY_DB_USER = "db.seutumaisa.username";
    private static final String PROPERTY_DB_PASSWORD = "db.seutumaisa.password";

    private static final Logger LOG = LogFactory.getLogger(SeutumaisaDBHelper.class);

    /**
     * Gets DB connection.
     * @return
     * @throws SQLException
     */
    private static final Connection getConnection() throws SQLException {
        String url = PropertyUtil.get(PROPERTY_DB_URL);
        String user = PropertyUtil.get(PROPERTY_DB_USER);
        String password = PropertyUtil.get(PROPERTY_DB_PASSWORD);
        Connection connection = DriverManager.getConnection(url, user, password);
        return connection;
    }

    /**
     * Gets search form fields.
     * @return
     * @throws JSONException
     */
    public static JSONArray getSearchFields() throws JSONException {
        JSONArray searchFields = new JSONArray();
        searchFields.put(getMaamassaSelect().toJSON());
        searchFields.put(getMaamassaRyhmaSelect().toJSON());
        searchFields.put(getKohdetyyppiSelect().toJSON());
        searchFields.put(getToteutunutAikatauluRange().toJSON());
        searchFields.put(getPilaantuneisuusSelect().toJSON());
        searchFields.put(getKuntaSelect().toJSON());

        return searchFields;
    }

    /**
     * Gets kunta select
     * @return
     */
    private static Select getKuntaSelect() {
        return getSelect("Kunta", "SELECT namefin as kunta FROM kuntarajat;", SeutumaisaHistorySearchHelper.KEY_KUNTA);
    }

    /**
     * Gets suunnitteluaika data range select
     * @return
     */
    private static DateRangeSelect getToteutunutAikatauluRange() {
        DateRangeSelect range = new DateRangeSelect();
        range.setId(SeutumaisaHistorySearchHelper.KEY_TOTEUTUNUTAIKATAULU);
        range.setTitle("Toteutunut aikataulu");
        return range;
    }

    /**
     * Gets kohdetyyppi select
     * @return
     */
    private static Select getKohdetyyppiSelect() {
        return getSelect("Kohdetyyppi", "SELECT kohdetyyppi FROM maamassakohde WHERE kohdetyyppi IS NOT NULL GROUP BY kohdetyyppi;", SeutumaisaHistorySearchHelper.KEY_KOHDETYYPPI);
    }

    /**
     * Gets maamassaryhma select
     * @return
     */
    private static Select getMaamassaRyhmaSelect() {
        return getSelect("Massan ryhmä", "SELECT maamassaryhma FROM maamassatieto WHERE maamassaryhma IS NOT NULL GROUP BY maamassaryhma;", SeutumaisaHistorySearchHelper.KEY_MAAMASSARYHMA);
    }

    /**
     * Gets maamassa select
     * @return
     */
    private static Select getMaamassaSelect() {
        return getSelect("Massan laji", "SELECT maamassalaji FROM maamassatieto WHERE maamassalaji IS NOT NULL GROUP BY maamassalaji;", SeutumaisaHistorySearchHelper.KEY_MAAMASSALAJI);
    }

    /**
     * Gets pilaantuneisuus select
     * @return
     */
    private static Select getPilaantuneisuusSelect() {
        return getSelect("Pilaantuneisuus", "SELECT pilaantuneisuus FROM maamassatieto WHERE pilaantuneisuus IS NOT NULL GROUP BY pilaantuneisuus;", SeutumaisaHistorySearchHelper.KEY_PILAANTUNEISUUS);
    }

    /**
     * Generic get select
     * @param title
     * @param sql
     * @param column
     * @return
     */
    private static Select getSelect(String title, String sql, String column) {
        Select select = new Select();
        select.setTitle(title);
        select.setId(column);
        select.setValues(getSelectValues(sql, column));
        return select;
    }

    /**
     * Generic get select
     * @param title
     * @param sql
     * @param column
     * @param idColumn
     * @return
     */
    private static Select getSelect(String title, String sql, String column, String idColumn) {
        Select select = new Select();
        select.setTitle(title);
        select.setId(column);
        select.setValues(getSelectValues(sql, column, idColumn));
        return select;
    }

    /**
     * Gets select values
     * @param sql
     * @param column
     * @return
     */
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

    /**
     * Gets select values
     * @param sql
     * @param column
     * @param idColumn
     * @return
     */
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


    /**
     * Handle search form database
     * @param params
     * @return
     * @throws JSONException
     * @throws ActionParamsException
     */
    public static JSONObject search(ActionParameters params) throws JSONException, ActionParamsException {
        DataTableResult result = new DataTableResult();

        // TODO: can we get geom index another way than hard coded ?
        result.setColumnDefs(HistoryDataTableHelper.getColumnDefs(8));
        result.setColumns(HistoryDataTableHelper.getColumns());

        Connection conn = null;

        JSONArray results = new JSONArray();
        String sql = "";
        String sqlWithParams = "";

        try  {
            conn = getConnection();


            //new
            StringBuilder sb = new StringBuilder();
            sb.append("SELECT DISTINCT maamassalaji, maamassaryhma, pilaantuneisuus, kohdetyyppi, namefin as kunta, SUM(maara) as maara");
            sb.append("FROM(SELECT maamassalaji, maamassaryhma, SUM(amount_remaining) as maara,");
            sb.append("pilaantuneisuus, mk.kohdetyyppi as kohdetyyppi,k.namefin,luotu as luotu");
            sb.append("FROM maamassakohde mk ");
            sb.append("LEFT JOIN maamassatieto mt ON mk.id = mt.maamassakohde_id ");
            sb.append("LEFT JOIN kuntarajat k ON st_contains(k.geom, mk.geom) ");
            sb.append("GROUP BY laji, ryhma, pilaantuneisuus, kohdetyyppi, namefin ,luotu");
            sb.append("UNION ALL");
            sb.append("SELECT maamassalaji, maamassaryhma, SUM(amount_remaining) as maara,");
            sb.append("pilaantuneisuus, mk.kohdetyyppi as kohdetyyppi,k.namefin,luotu as luotu");
            sb.append("FROM maamassakohde mk ");
            sb.append("LEFT JOIN maamassatieto_history mth ON mk.id = mth.maamassakohde_id ");
            sb.append("LEFT JOIN kuntarajat k ON st_contains(k.geom, mk.geom) ");
            sb.append("GROUP BY maamassalaji, maamassaryhma, pilaantuneisuus, kohdetyyppi, namefin ,luotu");
            sb.append(") as tulokset");
            sb.append("GROUP BY maamassalaji, maamassaryhma, pilaantuneisuus, kohdetyyppi, kunta");
            sb.append("ORDER BY maamassalaji");

            List<SearchParams> searchParams = SeutumaisaHistorySearchHelper.parseHistorySearchParams(params);
            sb.append(SeutumaisaHistorySearchHelper.getSearchWhere(searchParams));

            sql = sb.toString();

            PreparedStatement pstmt = conn.prepareStatement(sql);

            DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");

            int index = 1;
            for (int i=0; i<searchParams.size(); i++) {
                SearchParams searchParam = searchParams.get(i);
                if (searchParam.getValue() instanceof Integer) {
                    pstmt.setInt(index, (int)searchParam.getValue());
                    index++;
                } else if (searchParam.getValue() instanceof Long) {
                    pstmt.setLong(index, (long)searchParam.getValue());
                    index++;
                } else if (searchParam.getValue() instanceof String) {
                    pstmt.setString(index, (String)searchParam.getValue());
                    index++;
                } else if (searchParam.getValue() instanceof Range) {
                    Range range = (Range) searchParam.getValue();

                    if(range.getMin() != null && range.getMax() != null) {
                        if(range.getMin() instanceof Long && range.getMax() instanceof Long) {
                            pstmt.setLong(index, (long)range.getMin());
                            index++;
                            pstmt.setLong(index, (long)range.getMax());
                            index++;
                        } else if(range.getMin() instanceof Integer && range.getMax() instanceof Integer) {
                            pstmt.setInt(index, (int)range.getMin());
                            index++;
                            pstmt.setInt(index, (int)range.getMax());
                            index++;
                        } else if(range.getMin() instanceof Date && range.getMax() instanceof Date) {
                            pstmt.setDate(index, (Date)range.getMin());
                            index++;
                            pstmt.setDate(index, (Date)range.getMax());
                            index++;
                        } else if(range.getMin() instanceof String && range.getMax() instanceof String) {
                            pstmt.setString(index, (String)range.getMin());
                            index++;
                            pstmt.setString(index, (String)range.getMax());
                            index++;
                        }
                    } else if(range.getMin() != null){

                        if(range.getMin() instanceof Long) {
                            pstmt.setLong(index, (long)range.getMin());
                            index++;
                        } else if(range.getMin() instanceof Integer) {
                            pstmt.setInt(index, (int)range.getMin());
                            index++;
                        } else if(range.getMin() instanceof Date) {
                            pstmt.setDate(index, (Date)range.getMin());
                            index++;
                        } else if(range.getMin() instanceof String) {
                            pstmt.setString(index, (String)range.getMin());
                            index++;
                        }
                    } else if(range.getMax() != null){

                        if(range.getMax() instanceof Long) {
                            pstmt.setLong(index, (long)range.getMax());
                            index++;
                        } else if(range.getMax() instanceof Integer) {
                            pstmt.setInt(index, (int)range.getMax());
                            index++;
                        } else if(range.getMax() instanceof Date) {
                            pstmt.setDate(index, (Date)range.getMax());
                            index++;
                        } else if(range.getMax() instanceof String) {
                            pstmt.setString(index, (String)range.getMax());
                            index++;
                        }
                    }
                }
            }

            sqlWithParams = pstmt.toString();
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                JSONArray row = new JSONArray();
                row.put(rs.getString("maamassalaji"));
                row.put(rs.getString("maamassaryhma"));
                row.put(rs.getString("pilaantuneisuus"));
                row.put(rs.getString("kohdetyyppi"));
                row.put(rs.getString("kunta"));
                row.put(rs.getLong("maara"));
                results.put(row);
            }

        } catch (SQLException e) {
            LOG.error(e, "Cannot create SQL query, sql=" + sqlWithParams);

        } finally {
            if(conn != null) {
                try {
                    conn.close();
                } catch (Exception e) {
                    LOG.error(e);
                }
            }
        }
        result.setData(results);

        return result.toJSON();

    }


}
