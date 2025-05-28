package thesis.data.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public class TechnologyProperties {
    @NotBlank(message = "markerName cannot be empty")
    private String markerName;
    private Boolean isPercentage;
    private List<DeviationRange> deviationRanges;
    @Min(value = 0, message = "sensitivity must be between 0 and 100")
    @Max(value = 100, message = "sensitivity must be between 0 and 100")
    private Double sensitivity;
    @Min(value = 0, message = "specificity must be between 0 and 100")
    @Max(value = 100, message = "specificity must be between 0 and 100")
    private Double specificity;
    private List<String> comparableWith;

    public TechnologyProperties(String markerName, Boolean isPercentage, List<DeviationRange> deviationRanges, List<String> comparableWith) {
        this.markerName = markerName;
        this.isPercentage = isPercentage;
        this.deviationRanges = deviationRanges;
        this.comparableWith = comparableWith;
    }

    public TechnologyProperties() {
    }

    public String getMarkerName() {
        return markerName;
    }

    public void setMarkerName(String markerName) {
        this.markerName = markerName;
    }

    public Boolean getIsPercentage() {
        return isPercentage;
    }

    public void setIsPercentage(Boolean isPercentage) {
        this.isPercentage = isPercentage;
    }

    public List<DeviationRange> getDeviationRanges() {
        return deviationRanges;
    }

    public void setDeviationRanges(List<DeviationRange> deviationRanges) {
        this.deviationRanges = deviationRanges;
    }

    public Double getSensitivity() {
        return sensitivity;
    }

    public void setSensitivity(Double sensitivity) {
        this.sensitivity = sensitivity;
    }

    public Double getSpecificity() {
        return specificity;
    }

    public void setSpecificity(Double specificity) {
        this.specificity = specificity;
    }

    public List<String> getComparableWith() {
        return comparableWith;
    }

    public void setComparableWith(List<String> comparableWith) {
        this.comparableWith = comparableWith;
    }
}
