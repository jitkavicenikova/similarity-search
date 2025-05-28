package thesis.domain.search.dto;

import thesis.domain.search.dto.enums.NumericSearchType;

public class NumericSearchConfiguration {
    private String markerName;
    private String unitName;
    private Double minimum;
    private Double maximum;
    private Double minimumWithTolerance;
    private Double maximumWithTolerance;
    private Boolean withTolerance;
    private NumericSearchType searchType;
    private Boolean useTechnologyDeviation;

    public NumericSearchConfiguration() {
    }

    public NumericSearchConfiguration(String markerName, String unitName, Double minimum, Double maximum, Double minimumWithTolerance, Double maximumWithTolerance, NumericSearchType searchType, Boolean useTechnologyDeviation) {
        this.markerName = markerName;
        this.unitName = unitName;
        this.minimum = minimum;
        this.maximum = maximum;
        this.minimumWithTolerance = minimumWithTolerance;
        this.maximumWithTolerance = maximumWithTolerance;
        this.searchType = searchType;
        this.useTechnologyDeviation = useTechnologyDeviation;
    }

    public String getMarkerName() {
        return markerName;
    }

    public void setMarkerName(String markerName) {
        this.markerName = markerName;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public Double getMinimum() {
        return minimum;
    }

    public void setMinimum(Double minimum) {
        this.minimum = minimum;
    }

    public Double getMaximum() {
        return maximum;
    }

    public void setMaximum(Double maximum) {
        this.maximum = maximum;
    }

    public Double getMinimumWithTolerance() {
        return minimumWithTolerance;
    }

    public void setMinimumWithTolerance(Double minimumWithTolerance) {
        this.minimumWithTolerance = minimumWithTolerance;
    }

    public Double getMaximumWithTolerance() {
        return maximumWithTolerance;
    }

    public void setMaximumWithTolerance(Double maximumWithTolerance) {
        this.maximumWithTolerance = maximumWithTolerance;
    }

    public Boolean getWithTolerance() {
        return withTolerance;
    }

    public void setWithTolerance(Boolean withTolerance) {
        this.withTolerance = withTolerance;
    }

    public NumericSearchType getSearchType() {
        return searchType;
    }

    public void setSearchType(NumericSearchType searchType) {
        this.searchType = searchType;
    }

    public Boolean getUseTechnologyDeviation() {
        return useTechnologyDeviation;
    }

    public void setUseTechnologyDeviation(Boolean useTechnologyDeviation) {
        this.useTechnologyDeviation = useTechnologyDeviation;
    }
}
