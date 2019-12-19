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
public class SeutumaisaHistorySearchHelper {
    public static final String KEY_KUNTA = "kunta";
    public static final String KEY_KOHDETYYPPI = "kohdetyyppi";
    public static final String KEY_MAAMASSARYHMA = "maamassaryhma";
    public static final String KEY_MAAMASSALAJI = "maamassalaji";
    public static final String KEY_MAARA = "amount_remaining";
    public static final String KEY_START = "start";
    public static final String KEY_END = "end";
    //History search
    public static final String KEY_TOTEUTUNUTAIKATAULU = "realized_date";
    public static final String KEY_TOTEUTUNUTAIKATAULU_ALKU = "realized_begin_date";
    public static final String KEY_TOTEUTUNUTAIKATAULU_LOPPU = "realized_end_date";
    public static final String KEY_PILAANTUNEISUUS = "pilaantuneisuus";

    /**
     * Parse search params from json
     * @param params
     * @return
     * @throws JSONException
     * @throws ActionParamsException
     */
    public static List<SearchParams> parseHistorySearchParams(ActionParameters params) throws JSONException, ActionParamsException {
        JSONObject jsonParams =  params.getHttpParamAsJSON("params");

        List<SearchParams> searchParams = new ArrayList<>();

        if (jsonParams.has(KEY_KUNTA)) {
            SearchParams pKunta = new SearchParams("namefin", null, jsonParams.getString(KEY_KUNTA));
            pKunta.setColumnPrefix("k.");
            searchParams.add(pKunta);
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
            searchParams.add(pMaara);
        }

        if (jsonParams.has(KEY_KOHDETYYPPI)) {
            SearchParams pKohdetyyppi = new SearchParams(KEY_KOHDETYYPPI, null, jsonParams.getString(KEY_KOHDETYYPPI));
            pKohdetyyppi.setColumnPrefix("mk.");
            pKohdetyyppi.setNeedCastVarchar(true);
            searchParams.add(pKohdetyyppi);
        }

        if (jsonParams.has(KEY_MAAMASSARYHMA)) {
            SearchParams pMaamassaryhma = new SearchParams(KEY_MAAMASSARYHMA, null, jsonParams.getString(KEY_MAAMASSARYHMA));
            pMaamassaryhma.setNeedCastVarchar(true);
            searchParams.add(pMaamassaryhma);
        }

        if (jsonParams.has(KEY_MAAMASSALAJI)) {
            SearchParams pMaamassalaji = new SearchParams(KEY_MAAMASSALAJI, null, jsonParams.getString(KEY_MAAMASSALAJI));
            pMaamassalaji.setNeedCastVarchar(true);
            searchParams.add(pMaamassalaji);
        }

        if (jsonParams.has(KEY_PILAANTUNEISUUS)) {
            SearchParams pPilaantuneisuus = new SearchParams(KEY_PILAANTUNEISUUS, null, jsonParams.getString(KEY_PILAANTUNEISUUS));
            pPilaantuneisuus.setNeedCastVarchar(true);
            searchParams.add(pPilaantuneisuus);
        }

        if (jsonParams.has(KEY_TOTEUTUNUTAIKATAULU)) {
            Range rangeRealized = new Range();
            JSONObject jsonRealizedRange = jsonParams.getJSONObject(KEY_TOTEUTUNUTAIKATAULU);
            if(jsonRealizedRange.has(KEY_END)) {
                rangeRealized.setMax(jsonRealizedRange.getString(KEY_END));
                rangeRealized.setMaxColumn(KEY_TOTEUTUNUTAIKATAULU_LOPPU);
            }

            if(jsonRealizedRange.has(KEY_START)) {
                rangeRealized.setMin(jsonRealizedRange.getString(KEY_START));
                rangeRealized.setMinColumn(KEY_TOTEUTUNUTAIKATAULU_ALKU);
            }

            SearchParams pToteutunutAikataulu = new SearchParams(KEY_TOTEUTUNUTAIKATAULU, null, rangeRealized);
            pToteutunutAikataulu.setNeedCastVarchar(true);
            searchParams.add(pToteutunutAikataulu);
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
