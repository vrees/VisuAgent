package de.testo.cal.visuagent.service;

import de.testo.cal.visuagent.model.MeasurementResponse;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@Service
public class MeasurementService {

    public static final String PROMPT = """
            Extract the measurement value and unit from the image.
            Return the result as JSON using following fields:
            value (float) and unit (String)
            """  ;

    private final OpenAiServiceWrapper openAiServiceWrapper;

    public MeasurementService(OpenAiServiceWrapper openAiServiceWrapper) {
        this.openAiServiceWrapper = openAiServiceWrapper;
    }

    /**
     * Extracts measurement value and unit from the given image using AI (OpenAI).
     *
     * @param file   the image file (ROI)
     * @return measurement value and unit
     */
    public MeasurementResponse extractMeasurement(MultipartFile file) {
        try {
            String base64Image = Base64.getEncoder().encodeToString(file.getBytes());
            return extractMeasurement(base64Image);
        } catch (IOException e) {
            log.error("Error extracting measurement from base64 image", e);
            return new MeasurementResponse(-999999, "error");
        }
    }

    public MeasurementResponse extractMeasurement(String base64Image) {
        return openAiServiceWrapper.extractMeasurement(PROMPT, base64Image);
    }
}
