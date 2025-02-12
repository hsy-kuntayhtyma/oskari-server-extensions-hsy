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
import java.util.ArrayList;
import java.util.List;

/**
 * Helper for seutumasia db handling.
 */
public class SeutumaisaDBHelper {
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
        searchFields.put(getKelpoisuusluokkaSelect().toJSON());
        searchFields.put(getKohdetyyppiSelect().toJSON());
        searchFields.put(getMaamassantilaSelect().toJSON());
        searchFields.put(getSuunnitteluAikatauluRange().toJSON());
        searchFields.put(getMassanmaaraRange().toJSON());
        searchFields.put(getOmistajaSelect().toJSON());
        searchFields.put(getKuntaSelect().toJSON());

        return searchFields;
    }

    /**
     * Gets kunta select
     * @return
     */
    private static Select getKuntaSelect() {
        return getSelect("Kunta", "SELECT namefin as kunta FROM kuntarajat;", SeutumaisaSearchHelper.KEY_KUNTA);
    }

    /**
     * Gets omistaja select
     * @return
     */
    private static Select getOmistajaSelect() {
        return getSelect("Omistaja (massan)", "SELECT h.organisaatio, h.id FROM maamassakohde mk LEFT JOIN henkilo h ON h.id = mk.omistaja_id WHERE organisaatio IS NOT NULL GROUP BY organisaatio, h.id;", SeutumaisaSearchHelper.KEY_ORGANISAATIO, "id");
    }

    /**
     * Gets massan maara range select
     * @return
     */
    private static RangeSlider getMassanmaaraRange() {
        RangeSlider range = new RangeSlider();
        range.setId(SeutumaisaSearchHelper.KEY_MAARA);
        range.setTitle("Massan määrä");
        range.setMin(100);
        range.setMax(getMaxMaara());

        return range;
    }

    /**
     * Gets suunnitteluaika data range select
     * @return
     */
    private static DateRangeSelect getSuunnitteluAikatauluRange() {
        DateRangeSelect range = new DateRangeSelect();
        range.setId(SeutumaisaSearchHelper.KEY_SUUNNITTELUAIKATAULU);
        range.setTitle("Suunniteltu aikataulu");
        return range;
    }

    /**
     * Gets maamassantila select
     * @return
     */
    private static Select getMaamassantilaSelect() {
        return getSelect("Maamassan tila", "SELECT maamassatila FROM maamassatieto WHERE maamassatila IS NOT NULL GROUP BY maamassatila;", SeutumaisaSearchHelper.KEY_MAAMASSATILA);
    }

    /**
     * Gets kohdetyyppi select
     * @return
     */
    private static Select getKohdetyyppiSelect() {
        return getSelect("Kohdetyyppi", "SELECT kohdetyyppi FROM maamassakohde WHERE kohdetyyppi IS NOT NULL GROUP BY kohdetyyppi;", SeutumaisaSearchHelper.KEY_KOHDETYYPPI);
    }

    /**
     * Gets kelpoisuusluokksa select
     * @return
     */
    private static Select getKelpoisuusluokkaSelect() {
        return getSelect("Kelpoisuusluokka", "SELECT kelpoisuusluokka FROM maamassatieto WHERE kelpoisuusluokka IS NOT NULL GROUP BY kelpoisuusluokka;", SeutumaisaSearchHelper.KEY_KELPOISUUSLUOKKA);
    }

    /**
     * Gets maamassaryhma select
     * @return
     */
    private static Select getMaamassaRyhmaSelect() {
        return getSelect("Massan ryhmä", "SELECT maamassaryhma FROM maamassatieto WHERE maamassaryhma IS NOT NULL GROUP BY maamassaryhma;", SeutumaisaSearchHelper.KEY_MAAMASSARYHMA);
    }

    /**
     * Gets maamassa select
     * @return
     */
    private static Select getMaamassaSelect() {
        return getSelect("Massan laji", "SELECT maamassalaji FROM maamassatieto WHERE maamassalaji IS NOT NULL GROUP BY maamassalaji;", SeutumaisaSearchHelper.KEY_MAAMASSALAJI);
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
     * Gets max maara value
     * @return
     */
    private static int getMaxMaara() {
        int maara = 0;

        Connection conn = null;

        try  {
            conn = getConnection();

            PreparedStatement preparedStatement = conn.prepareStatement("SELECT min(" + SeutumaisaSearchHelper.KEY_MAARA +
                    "), max(" + SeutumaisaSearchHelper.KEY_MAARA + ") FROM maamassatieto WHERE " + SeutumaisaSearchHelper.KEY_MAARA +" IS NOT NULL;");
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

        result.setColumnDefs(DataTableHelper.getColumnDefs());
        result.setColumns(DataTableHelper.getColumns());

        Connection conn = null;

        JSONArray results = new JSONArray();
        String sql = "";
        String sqlWithParams = "";

        try  {
            conn = getConnection();

            StringBuilder sb = new StringBuilder();
            sb.append("SELECT mk.id as kohde_id, mk.nimi as kohde_nimi, mt.maamassalaji, mt.maamassaryhma,");
            sb.append("mt.kelpoisuusluokka, mk.kohdetyyppi,");
            sb.append("mt.maamassatila, mt.planned_begin_date, mt.planned_end_date,");
            sb.append("mt.amount_remaining, h.nimi, h.email, h.puhelin, h.organisaatio,");
            sb.append("k.namefin as kunta,");
            sb.append("ST_AsGeoJSON(mk.geom) geojson, mk.omistaja_id as organisaatio_id ");
            sb.append("FROM maamassakohde mk ");
            sb.append("LEFT JOIN maamassatieto mt ON mk.id = mt.maamassakohde_id ");
            sb.append("LEFT JOIN henkilo h ON h.id = mk.omistaja_id ");
            sb.append("LEFT JOIN kuntarajat k ON mk.kunta = k.natcode ");
            List<SearchParams> searchParams = SeutumaisaSearchHelper.parseSearchParams(params);
            sb.append(SeutumaisaSearchHelper.getSearchWhere(searchParams));

            sql = sb.toString();

            PreparedStatement pstmt = conn.prepareStatement(sql);

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
                row.put(rs.getString("kohde_id"));
                row.put(rs.getString("kohde_nimi"));
                row.put(rs.getString("maamassalaji"));
                row.put(rs.getString("maamassaryhma"));
                row.put(rs.getString("kelpoisuusluokka"));
                row.put(rs.getString("kohdetyyppi"));
                row.put(rs.getString("maamassatila"));
                row.put(rs.getDate("planned_begin_date"));
                row.put(rs.getDate("planned_end_date"));
                row.put(rs.getLong("amount_remaining"));
                row.put(rs.getString("nimi"));
                row.put(rs.getString("email"));
                row.put(rs.getString("puhelin"));
                row.put(rs.getString("organisaatio"));
                row.put(rs.getString("kunta"));
                row.put(rs.getString("geojson"));
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
