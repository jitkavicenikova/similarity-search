package thesis.domain.processing.dto;

import java.util.List;

/**
 * Represents a technology and its properties.
 */
public class TechnologyDto {
    private String name;
    private List<TechnologyPropertiesDto> properties;

    public TechnologyDto() {
    }

    public TechnologyDto(String name, List<TechnologyPropertiesDto> properties) {
        this.name = name;
        this.properties = properties;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<TechnologyPropertiesDto> getProperties() {
        return properties;
    }

    public void setProperties(List<TechnologyPropertiesDto> properties) {
        this.properties = properties;
    }
}