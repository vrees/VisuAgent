package de.testo.cal.visuagent.model;

/**
 * Model for measurement extraction response.
 *
 * @author GitHub Copilot
 */
public class MeasurementResponse {

    public float value;

    public float confidence;

    public MeasurementResponse() {
        // Default constructor for Jackson
    }

    public MeasurementResponse(float value, float confidence) {
        this.value = value;
        this.confidence = confidence;
    }


    @Override
    public String toString() {
        return value + " (confidence: " + confidence + ")";
    }
}
