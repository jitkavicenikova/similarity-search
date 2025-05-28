package thesis.domain.processing.dto;

import java.util.List;

/**
 * Represents a unit with a name, nameRaw, and conversions.
 */
public class UnitDto {
    private String name;
    private String nameRaw;
    private List<ConversionDto> conversions;

    public UnitDto() {
    }

    public UnitDto(String name, String nameRaw) {
        this.name = name;
        this.nameRaw = nameRaw;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameRaw() {
        return nameRaw;
    }

    public void setNameRaw(String nameRaw) {
        this.nameRaw = nameRaw;
    }

    public List<ConversionDto> getConversions() {
        return conversions;
    }

    public void setConversions(List<ConversionDto> conversions) {
        this.conversions = conversions;
    }
}
