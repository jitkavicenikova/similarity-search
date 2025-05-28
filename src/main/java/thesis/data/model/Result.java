package thesis.data.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.time.LocalDateTime;

@RedisHash("result")
public class Result implements Identifiable {
    @Id
    private String id; // recordId:markerName
    @Indexed
    private String recordId;
    @Indexed
    @NotBlank(message = "markerName cannot be empty")
    private String markerName;

    @NotNull(message = "recordIdRaw cannot be null")
    private Integer recordIdRaw;

    private Double min;
    private Double max;
    @Indexed
    @Size(max = 100, message = "stringValue must be at most 100 characters long")
    private String stringValue;
    @Indexed
    private String stringValueCategory;
    @Indexed
    private Boolean booleanValue;
    @Indexed
    private String technologyName;
    @Size(max = 100, message = "sample must be at most 100 characters long")
    private String sample;
    private Double minRaw;
    private Double maxRaw;
    private String unitRawName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    public Result(Integer recordIdRaw, String markerName, Double min, Double max,
                  String stringValue, String stringValueCategory, Boolean booleanValue, String sample,
                  String technologyName, Double minRaw, Double maxRaw, String unitRawName, LocalDateTime timestamp) {
        this.recordIdRaw = recordIdRaw;
        this.markerName = markerName;
        this.min = min;
        this.max = max;
        this.stringValue = stringValue;
        this.stringValueCategory = stringValueCategory;
        this.booleanValue = booleanValue;
        this.sample = sample;
        this.technologyName = technologyName;
        this.minRaw = minRaw;
        this.maxRaw = maxRaw;
        this.unitRawName = unitRawName;
        this.timestamp = timestamp;
    }

    public Result() {
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public Integer getRecordIdRaw() {
        return recordIdRaw;
    }

    public void setRecordIdRaw(Integer recordIdRaw) {
        this.recordIdRaw = recordIdRaw;
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
