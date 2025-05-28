package thesis.domain.processing.dto;

import thesis.data.enums.AggregationType;

import java.util.List;

/**
 * Represents a marker with various attributes such as name, description, raw name and unit.
 */
public class MarkerDto {
    private String name;
    private String description;
    private String nameRaw;
    private String unitName;

    private List<String> childMarkerNames;
    private AggregationType aggregationType;

    public MarkerDto() {
    }

    public MarkerDto(String name, String description, String nameRaw, String unitName) {
        this.name = name;
        this.description = description;
        this.nameRaw = nameRaw;
        this.unitName = unitName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNameRaw() {
        return nameRaw;
    }

    public void setNameRaw(String nameRaw) {
        this.nameRaw = nameRaw;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public List<String> getChildMarkerNames() {
        return childMarkerNames;
    }

    public void setChildMarkerNames(List<String> childMarkerNames) {
        this.childMarkerNames = childMarkerNames;
    }

    public AggregationType getAggregationType() {
        return aggregationType;
    }

    public void setAggregationType(AggregationType aggregationType) {
        this.aggregationType = aggregationType;
    }
}
