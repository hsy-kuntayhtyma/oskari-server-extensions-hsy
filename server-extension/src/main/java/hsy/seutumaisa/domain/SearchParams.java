package hsy.seutumaisa.domain;

/**
 * Handles seutumaisa search parameters
 */

public class SearchParams {
    private String id;
    private String title;
    private Object value;
    private String columnPrefix;
    private boolean needCastVarchar;

    public SearchParams() {}

    public SearchParams(final String id, final String title, final Object value) {
        this.id = id;
        this.title = title;
        this.value = value;
    }

    public SearchParams(final String id, final String title) {
        this.id = id;
        this.title = title;
    }

    public SearchParams(final String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public String getMinId() {
        if(value instanceof Range) {
            Range range = (Range) value;
            if(range.getMinColumn() != null) {
                return range.getMinColumn();
            }
            return id;
        }
        return id;
    }

    public String getMaxId() {
        if(value instanceof Range) {
            Range range = (Range) value;
            if(range.getMaxColumn() != null) {
                return range.getMaxColumn();
            }
            return id;
        }
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getColumnPrefix() {
        return columnPrefix;
    }

    public void setColumnPrefix(String columnPrefix) {
        this.columnPrefix = columnPrefix;
    }

    public boolean isNeedCastVarchar() {
        return needCastVarchar;
    }

    public void setNeedCastVarchar(boolean needCastVarchar) {
        this.needCastVarchar = needCastVarchar;
    }
}
