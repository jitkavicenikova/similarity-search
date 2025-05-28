package thesis.domain.search.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class BoolSearchOptions {
    @NotNull(message = "markerName cannot be null")
    private String markerName;
    @NotNull(message = "value cannot be null")
    private Boolean value;

    @Min(value = 0, message = "minSensitivity must be between 0 and 100")
    @Max(value = 100, message = "minSensitivity must be between 0 and 100")
    private Double minSensitivity;
    @Min(value = 0, message = "minSpecificity must be between 0 and 100")
    @Max(value = 100, message = "minSpecificity must be between 0 and 100")
    private Double minSpecificity;

    private SearchFilters filters;

    public String getMarkerName() {
        return markerName;
    }

    public void setMarkerName(String markerName) {
        this.markerName = markerName;
    }

    public Boolean getValue() {
        return value;
    }

    public void setValue(Boolean value) {
        this.value = value;
    }

    public Double getMinSensitivity() {
        return minSensitivity;
    }

    public void setMinSensitivity(Double minSensitivity) {
        this.minSensitivity = minSensitivity;
    }

    public Double getMinSpecificity() {
        return minSpecificity;
    }

    public void setMinSpecificity(Double minSpecificity) {
        this.minSpecificity = minSpecificity;
    }

    public SearchFilters getFilters() {
        return filters;
    }

    public void setFilters(SearchFilters filters) {
        this.filters = filters;
    }
}
