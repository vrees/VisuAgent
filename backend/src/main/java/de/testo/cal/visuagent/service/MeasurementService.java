package de.testo.cal.visuagent.service;

import de.testo.cal.visuagent.model.MeasurementResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Service for measurement extraction logic.
 *
 * @author GitHub Copilot
 */
@Service
public class MeasurementService {
    private final OpenAiServiceWrapper openAiServiceWrapper;

    public MeasurementService(OpenAiServiceWrapper openAiServiceWrapper) {
        this.openAiServiceWrapper = openAiServiceWrapper;
    }

    /**
     * Extracts measurement value and unit from the given image using AI (OpenAI).
     *
     * @param file the image file (ROI)
     * @param prompt the prompt for the AI
     * @return measurement value and unit
     */
    public MeasurementResponse extractMeasurement(MultipartFile file, String prompt) {
        try {
            String base64Image = Base64.getEncoder().encodeToString(file.getBytes());
            String result = openAiServiceWrapper.extractMeasurement(prompt, base64Image);
            // Parse result, e.g. "23.5 Newton" or "12.3 Volt"
            Pattern p = Pattern.compile("([\\d.,]+)\\s*([A-Za-z]+)");
            Matcher m = p.matcher(result);
            if (m.find()) {
                return new MeasurementResponse(m.group(1), m.group(2));
            } else {
                return new MeasurementResponse("?", "?");
            }
        } catch (IOException e) {
            // Fehlerbehandlung
            return new MeasurementResponse("error", "error");
        }
    }
}
