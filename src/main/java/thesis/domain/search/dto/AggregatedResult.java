package thesis.domain.search.dto;

import java.util.List;

public class AggregatedResult {
    private Double min;
    private Double max;
    private String recordId;
    private List<String> markerNames;
    private List<String> resultIds;

    public AggregatedResult(String recordId, Double min, Double max, List<String> markerNames, List<String> resultIds) {
        this.recordId = recordId;
        this.min = min;
        this.max = max;
        this.markerNames = markerNames;
        this.resultIds = resultIds;
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

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public List<String> getMarkerNames() {
        return markerNames;
    }

    public void setMarkerNames(List<String> markerNames) {
        this.markerNames = markerNames;
    }

    public List<String> getResultIds() {
        return resultIds;
    }

    public void setResultIds(List<String> resultIds) {
        this.resultIds = resultIds;
    }
}
