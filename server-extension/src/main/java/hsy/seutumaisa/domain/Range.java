package hsy.seutumaisa.domain;

/**
 * Search para range type
 */
public class Range {
    private Object min;
    private Object max;
    private String minColumn;
    private String maxColumn;

    public Object getMin() {
        return min;
    }

    public void setMin(Object min) {
        this.min = min;
    }

    public Object getMax() {
        return max;
    }

    public void setMax(Object max) {
        this.max = max;
    }

    public String getMinColumn() {
        return minColumn;
    }

    public void setMinColumn(String minColumn) {
        this.minColumn = minColumn;
    }

    public String getMaxColumn() {
        return maxColumn;
    }

    public void setMaxColumn(String maxColumn) {
        this.maxColumn = maxColumn;
    }
}
