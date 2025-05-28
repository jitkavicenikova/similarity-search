package thesis.domain.processing.dto;

/**
 * Represents a conversion formula between units.
 */
public class ConversionDto {
    private String targetUnitName;
    private String markerName;
    private String formula;

    public ConversionDto() {
    }

    public ConversionDto(String targetUnitName, String markerName, String formula) {
        this.targetUnitName = targetUnitName;
        this.markerName = markerName;
        this.formula = formula;
    }

    public String getTargetUnitName() {
        return targetUnitName;
    }

    public void setTargetUnitName(String targetUnitName) {
        this.targetUnitName = targetUnitName;
    }

    public String getMarkerName() {
        return markerName;
    }

    public void setMarkerName(String markerName) {
        this.markerName = markerName;
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }
}