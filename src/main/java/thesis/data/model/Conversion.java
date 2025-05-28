package thesis.data.model;

import jakarta.validation.constraints.NotBlank;

public class Conversion {
    @NotBlank(message = "targetUnitName cannot be empty")
    private String targetUnitName;
    @NotBlank(message = "markerName cannot be empty")
    private String markerName;
    @NotBlank(message = "formula cannot be empty")
    private String formula;

    public Conversion(String targetUnitName, String markerName, String formula) {
        this.targetUnitName = targetUnitName;
        this.markerName = markerName;
        this.formula = formula;
    }

    public Conversion() {
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