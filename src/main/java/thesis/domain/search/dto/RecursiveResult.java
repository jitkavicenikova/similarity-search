package thesis.domain.search.dto;

public class RecursiveResult {
    private Double min;
    private Double max;
    private String recordId;
    private String markerName;
    private String resultId;

    public RecursiveResult(String recordId, Double min, Double max) {
        this.recordId = recordId;
        this.min = min;
        this.max = max;
    }

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public Double getMin() {
        return min;
    }

    public void setMin(Double min) {
        this.min = min;
    }

    public Double getMax() {
        return max;
    }

    public void setMax(Double max) {
        this.max = max;
    }

    public String getMarkerName() {
        return markerName;
    }

    public void setMarkerName(String markerName) {
        this.markerName = markerName;
    }

    public String getResultId() {
        return resultId;
    }

    public void setResultId(String resultId) {
        this.resultId = resultId;
    }
}
