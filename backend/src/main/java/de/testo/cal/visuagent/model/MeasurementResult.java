package de.testo.cal.visuagent.model;

public class MeasurementResult {

    public float value;

    public String unit;


    @Override
    public String toString() {
        return value + " " + unit;
    }
}
