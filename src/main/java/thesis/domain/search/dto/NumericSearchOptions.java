package thesis.domain.search.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import thesis.domain.search.dto.enums.NumericSearchType;

public class NumericSearchOptions {
    @NotNull(message = "Marker name must be provided")
    private String markerName;
    private String unitName;
    private Double value;
    private Double minimum;
    private Double maximum;
    @PositiveOrZero(message = "Absolute deviation must be positive or zero")
    private Double absoluteDeviation;
    @PositiveOrZero(message = "Percentage deviation must be positive or zero")
    private Double percentageDeviation;

    @PositiveOrZero(message = "Absolute tolerance must be positive or zero")
    private Double absoluteTolerance;
    @PositiveOrZero(message = "Percentage tolerance must be positive or zero")
    private Double percentageTolerance;
    private NumericSearchType searchType;
    private Boolean useTechnologyDeviation;

    private SearchFilters filters;

    public @NotNull String getMarkerName() {
        return markerName;
    }

    public void setMarkerName(@NotNull String markerName) {
        this.markerName = markerName;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
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

    public Double getAbsoluteDeviation() {
        return absoluteDeviation;
    }

    public void setAbsoluteDeviation(Double absoluteDeviation) {
        this.absoluteDeviation = absoluteDeviation;
    }

    public Double getPercentageDeviation() {
        return percentageDeviation;
    }

    public void setPercentageDeviation(Double percentageDeviation) {
        this.percentageDeviation = percentageDeviation;
    }

    public Double getAbsoluteTolerance() {
        return absoluteTolerance;
    }

    public void setAbsoluteTolerance(Double absoluteTolerance) {
        this.absoluteTolerance = absoluteTolerance;
    }

    public Double getPercentageTolerance() {
        return percentageTolerance;
    }

    public void setPercentageTolerance(Double percentageTolerance) {
        this.percentageTolerance = percentageTolerance;
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

    public SearchFilters getFilters() {
        return filters;
    }

    public void setFilters(SearchFilters filters) {
        this.filters = filters;
    }
}
