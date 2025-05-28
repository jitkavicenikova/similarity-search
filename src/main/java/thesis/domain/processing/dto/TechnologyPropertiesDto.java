package thesis.domain.processing.dto;

import java.util.List;

/**
 * Represents a technology properties for a marker.
 */
public class TechnologyPropertiesDto {
    private String markerName;
    private Boolean isPercentage;
    private List<DeviationRangeDto> deviationRanges;

    private Double sensitivity;
    private Double specificity;
    private List<String> comparableWith;

    public TechnologyPropertiesDto() {
    }

    public TechnologyPropertiesDto(String markerName, Boolean isPercentage, List<DeviationRangeDto> deviationRanges, List<String> comparableWith) {
        this.markerName = markerName;
        this.isPercentage = isPercentage;
        this.deviationRanges = deviationRanges;
        this.comparableWith = comparableWith;
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

    public List<DeviationRangeDto> getDeviationRanges() {
        return deviationRanges;
    }

    public void setDeviationRanges(List<DeviationRangeDto> deviationRanges) {
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
