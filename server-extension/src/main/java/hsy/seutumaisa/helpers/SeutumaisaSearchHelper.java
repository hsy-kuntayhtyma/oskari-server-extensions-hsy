package hsy.seutumaisa.helpers;

import fi.nls.oskari.control.ActionParameters;
import fi.nls.oskari.control.ActionParamsException;
import hsy.seutumaisa.domain.Range;
import hsy.seutumaisa.domain.SearchParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Search helper
 */
public class SeutumaisaSearchHelper {
    public static final String KEY_ID = "id";
    public static final String KEY_KUNTA = "kunta";
    public static final String KEY_HANKEALUE = "hankealue";
    public static final String KEY_ORGANISAATIO = "organisaatio";
    public static final String KEY_SUUNNITTELUAIKATAULU = "planned_date";
    public static final String KEY_SUUNNITTELUAIKATAULU_ALKU = "planned_begin_date";
    public static final String KEY_SUUNNITTELUAIKATAULU_LOPPU = "planned_end_date";
    public static final String KEY_MAAMASSATILA = "maamassatila";
    public static final String KEY_KOHDETYYPPI = "kohdetyyppi";
    public static final String KEY_KELPOISUUSLUOKKA = "kelpoisuusluokka";
    public static final String KEY_PILAANTUNEISUUS = "pilaantuneisuus";
    public static final String KEY_MAAMASSARYHMA = "maamassaryhma";
    public static final String KEY_MAAMASSALAJI = "maamassalaji";
    public static final String KEY_MAARA = "amount_remaining";
    public static final String KEY_START = "start";
    public static final String KEY_END = "end";
    //History search
    public static final String KEY_TOTEUTUNUT_ALKU = "realized_begin_date";
    public static final String KEY_TOTEUTUNUT_LOPPU = "realized_end_date";

    /**
     * Parse search params from json
     * @param params
     * @return
     * @throws JSONException
     * @throws ActionParamsException
     */
    public static List<SearchParams> parseSearchParams(ActionParameters params) throws JSONException, ActionParamsException {
        JSONObject jsonParams =  params.getHttpParamAsJSON("params");

        List<SearchParams> searchParams = new ArrayList<>();

        if (jsonParams.has(KEY_ID)) {
            try {
                long idValue = Long.parseLong(jsonParams.getString(KEY_ID));
                SearchParams pId = new SearchParams("id", null, idValue);
                pId.setColumnPrefix("mk.");
                searchParams.add(pId);
            } catch (NumberFormatException e) {
                // Return 0 matches...
            }
        }

        if (jsonParams.has(KEY_KUNTA)) {
            String kunta = jsonParams.get(KEY_KUNTA).toString();
            SearchParams pKunta = new SearchParams("namefin", null, kunta);
            pKunta.setColumnPrefix("k.");
            pKunta.setNeedCastVarchar(true);
            searchParams.add(pKunta);
        }

        if (jsonParams.has(KEY_HANKEALUE)) {
            SearchParams pHankealue = new SearchParams("id", null, jsonParams.getInt(KEY_HANKEALUE));
            pHankealue.setColumnPrefix("ha.");
            searchParams.add(pHankealue);
        }

        if (jsonParams.has(KEY_ORGANISAATIO)) {
            SearchParams pOrganisaatio = new SearchParams("omistaja_id", null, jsonParams.getInt(KEY_ORGANISAATIO));
            pOrganisaatio.setColumnPrefix("mk.");
            searchParams.add(pOrganisaatio);
        }

        if (jsonParams.has(KEY_MAARA)) {
            Range range = new Range();
            JSONObject jsonRange = jsonParams.getJSONObject(KEY_MAARA);
            if(jsonRange.has(KEY_END)) {
                range.setMax(jsonRange.getInt(KEY_END));
            }

            if(jsonRange.has(KEY_START)) {
                range.setMin(jsonRange.getInt(KEY_START));
            }

            SearchParams pMaara = new SearchParams(KEY_MAARA, null, range);
            pMaara.setColumnPrefix("mt.");
            searchParams.add(pMaara);
        }

        if (jsonParams.has(KEY_SUUNNITTELUAIKATAULU)) {
            Range rangePlanned = new Range();
            JSONObject jsonPlannedRange = jsonParams.getJSONObject(KEY_SUUNNITTELUAIKATAULU);
            if(jsonPlannedRange.has(KEY_END)) {
                rangePlanned.setMax(jsonPlannedRange.getString(KEY_END));
                rangePlanned.setMaxColumn(KEY_SUUNNITTELUAIKATAULU_LOPPU);
            }

            if(jsonPlannedRange.has(KEY_START)) {
                rangePlanned.setMin(jsonPlannedRange.getString(KEY_START));
                rangePlanned.setMinColumn(KEY_SUUNNITTELUAIKATAULU_ALKU);
            }

            SearchParams pSuunnitteluAikataulu = new SearchParams(KEY_SUUNNITTELUAIKATAULU, null, rangePlanned);
            pSuunnitteluAikataulu.setColumnPrefix("mt.");
            pSuunnitteluAikataulu.setNeedCastVarchar(true);
            searchParams.add(pSuunnitteluAikataulu);
        }

        if (jsonParams.has(KEY_MAAMASSATILA)) {
            SearchParams pMaamassatila = new SearchParams(KEY_MAAMASSATILA, null, jsonParams.getString(KEY_MAAMASSATILA));
            pMaamassatila.setColumnPrefix("mt.");
            pMaamassatila.setNeedCastVarchar(true);
            searchParams.add(pMaamassatila);
        }

        if (jsonParams.has(KEY_KOHDETYYPPI)) {
            SearchParams pKohdetyyppi = new SearchParams(KEY_KOHDETYYPPI, null, jsonParams.getString(KEY_KOHDETYYPPI));
            pKohdetyyppi.setColumnPrefix("mk.");
            pKohdetyyppi.setNeedCastVarchar(true);
            searchParams.add(pKohdetyyppi);
        }

        if (jsonParams.has(KEY_KELPOISUUSLUOKKA)) {
            SearchParams pKelpoisuusluokka = new SearchParams(KEY_KELPOISUUSLUOKKA, null, jsonParams.getString(KEY_KELPOISUUSLUOKKA));
            pKelpoisuusluokka.setColumnPrefix("mt.");
            pKelpoisuusluokka.setNeedCastVarchar(true);
            searchParams.add(pKelpoisuusluokka);
        }

        if (jsonParams.has(KEY_PILAANTUNEISUUS)) {
            SearchParams pPilaantuneisuus = new SearchParams(KEY_PILAANTUNEISUUS, null, jsonParams.getString(KEY_PILAANTUNEISUUS));
            pPilaantuneisuus.setColumnPrefix("mt.");
            pPilaantuneisuus.setNeedCastVarchar(true);
            searchParams.add(pPilaantuneisuus);
        }

        if (jsonParams.has(KEY_MAAMASSARYHMA)) {
            SearchParams pMaamassaryhma = new SearchParams(KEY_MAAMASSARYHMA, null, jsonParams.getString(KEY_MAAMASSARYHMA));
            pMaamassaryhma.setColumnPrefix("mt.");
            pMaamassaryhma.setNeedCastVarchar(true);
            searchParams.add(pMaamassaryhma);
        }

        if (jsonParams.has(KEY_MAAMASSALAJI)) {
            SearchParams pMaamassalaji = new SearchParams(KEY_MAAMASSALAJI, null, jsonParams.getString(KEY_MAAMASSALAJI));
            pMaamassalaji.setColumnPrefix("mt.");
            pMaamassalaji.setNeedCastVarchar(true);
            searchParams.add(pMaamassalaji);
        }

        return searchParams;

    }

    /**
     * Genereates search where clause
     * @param searchParams
     * @return
     */
    public static String getSearchWhere(final List<SearchParams> searchParams){
        StringBuilder sb = new StringBuilder();
        if(searchParams.size() == 0) {
            return sb.toString();
        }

        sb.append("WHERE ");
        for (SearchParams searchParam : searchParams) {
            if (searchParam.getValue() instanceof Integer) {
                sb.append(searchParam.getColumnPrefix() + searchParam.getId() + "=? AND ");
            } else if (searchParam.getValue() instanceof Long) {
                sb.append(searchParam.getColumnPrefix() + searchParam.getId() + "=? AND ");
            } else if (searchParam.getValue() instanceof String) {
                if(searchParam.isNeedCastVarchar()) {
                    sb.append(searchParam.getColumnPrefix() + searchParam.getId() + "::VARCHAR =? AND ");
                } else {
                    sb.append(searchParam.getColumnPrefix() + searchParam.getId() + "=? AND ");
                }
            } else if (searchParam.getValue() instanceof Range) {
                Range range = (Range) searchParam.getValue();
                if(searchParam.isNeedCastVarchar()) {
                    if (range.getMin() != null && range.getMax() != null) {
                        sb.append(searchParam.getColumnPrefix() + searchParam.getMinId() + "::VARCHAR >= ? AND " + searchParam.getColumnPrefix() + searchParam.getMaxId() + "::VARCHAR <= ? AND ");
                    } else if (range.getMin() != null) {
                        sb.append(searchParam.getColumnPrefix() + searchParam.getMinId() + "::VARCHAR >= ? AND ");
                    } else if (range.getMax() != null) {
                        sb.append(searchParam.getColumnPrefix() + searchParam.getMaxId() + "::VARCHAR <= ? AND ");
                    }
                } else {
                    if (range.getMin() != null && range.getMax() != null) {
                        sb.append(searchParam.getColumnPrefix() + searchParam.getMinId() + ">= ? AND " + searchParam.getColumnPrefix() + searchParam.getMaxId() + "<= ? AND ");
                    } else if (range.getMin() != null) {
                        sb.append(searchParam.getColumnPrefix() + searchParam.getMinId() + ">= ? AND ");
                    } else if (range.getMax() != null) {
                        sb.append(searchParam.getColumnPrefix() + searchParam.getMaxId() + "<= ? AND ");
                    }
                }
            }

        }
        String where = sb.toString();
        where = where.substring(0, where.length()-4);

        return where;
    }

}
