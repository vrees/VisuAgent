package de.testo.cal.visuagent.model;

/**
 * Model for measurement extraction response.
 *
 * @author GitHub Copilot
 */
public class MeasurementResponse {
    private String value;
    private String unit;

    public MeasurementResponse() {}
    public MeasurementResponse(String value, String unit) {
        this.value = value;
        this.unit = unit;
    }

    // Getter und Setter
    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
}
