package de.testo.cal.visuagent.model;

/**
 * Model for measurement extraction response.
 *
 * @author GitHub Copilot
 */
public class MeasurementResponse {

    public float value;

    public String unit;

    public MeasurementResponse() {
        // Default constructor for Jackson
    }

    public MeasurementResponse(float value, String unit) {
        this.value = value;
        this.unit = unit;
    }


    @Override
    public String toString() {
        return value + " " + unit;
    }
}
