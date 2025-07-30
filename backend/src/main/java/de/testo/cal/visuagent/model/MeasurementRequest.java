package de.testo.cal.visuagent.model;

/**
 * Model for measurement extraction request.
 *
 * @author GitHub Copilot
 */
public class MeasurementRequest {
    private byte[] image;
    private String prompt;

    // Getter und Setter
    public byte[] getImage() { return image; }
    public void setImage(byte[] image) { this.image = image; }
    public String getPrompt() { return prompt; }
    public void setPrompt(String prompt) { this.prompt = prompt; }
}
