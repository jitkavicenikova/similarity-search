package thesis.domain.processing.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

/**
 * This class encapsulates the result data, including identifiers, measurements, units and additional information.
 */
public class ResultDto {
    private Integer recordId;
    private String markerName;

    private Double min;
    private Double max;

    private String stringValue;
    private String stringValueCategory;
    private Boolean booleanValue;

    private String technologyName;
    private String sample;

    private Double minRaw;
    private Double maxRaw;
    private String unitRawName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    public ResultDto() {
    }

    public ResultDto(Integer recordId, String markerName, Double min, Double max,
                     Double minRaw, Double maxRaw, String unitRawName, String technologyName, String sample) {
        this.recordId = recordId;
        this.markerName = markerName;
        this.min = min;
        this.max = max;
        this.minRaw = minRaw;
        this.maxRaw = maxRaw;
        this.unitRawName = unitRawName;
        this.technologyName = technologyName;
        this.sample = sample;
    }

    public ResultDto(Integer recordId, String markerName, String stringValue, String stringValueCategory, String technologyName, String sample) {
        this.recordId = recordId;
        this.markerName = markerName;
        this.stringValue = stringValue;
        this.stringValueCategory = stringValueCategory;
        this.technologyName = technologyName;
        this.sample = sample;
    }

    public ResultDto(Integer recordId, String markerName, Boolean booleanValue, String technologyName, String sample) {
        this.recordId = recordId;
        this.markerName = markerName;
        this.booleanValue = booleanValue;
        this.technologyName = technologyName;
        this.sample = sample;
    }

    public Integer getRecordId() {
        return recordId;
    }

    public void setRecordId(Integer recordId) {
        this.recordId = recordId;
    }

    public String getMarkerName() {
        return markerName;
    }

    public void setMarkerName(String markerName) {
        this.markerName = markerName;
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

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    public String getStringValueCategory() {
        return stringValueCategory;
    }

    public void setStringValueCategory(String stringValueCategory) {
        this.stringValueCategory = stringValueCategory;
    }

    public Boolean getBooleanValue() {
        return booleanValue;
    }

    public void setBooleanValue(Boolean booleanValue) {
        this.booleanValue = booleanValue;
    }

    public String getSample() {
        return sample;
    }

    public void setSample(String sample) {
        this.sample = sample;
    }

    public Double getMinRaw() {
        return minRaw;
    }

    public void setMinRaw(Double minRaw) {
        this.minRaw = minRaw;
    }

    public Double getMaxRaw() {
        return maxRaw;
    }

    public void setMaxRaw(Double maxRaw) {
        this.maxRaw = maxRaw;
    }

    public String getUnitRawName() {
        return unitRawName;
    }

    public void setUnitRawName(String unitRawName) {
        this.unitRawName = unitRawName;
    }

    public String getTechnologyName() {
        return technologyName;
    }

    public void setTechnologyName(String technologyName) {
        this.technologyName = technologyName;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
