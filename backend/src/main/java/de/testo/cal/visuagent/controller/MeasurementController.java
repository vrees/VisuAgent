package de.testo.cal.visuagent.controller;

import de.testo.cal.visuagent.model.MeasurementResponse;
import de.testo.cal.visuagent.service.MeasurementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

/**
 * REST controller for measurement extraction endpoint.
 *
 * @author GitHub Copilot
 */
@RestController
@RequestMapping("/api/measurements")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class MeasurementController {

    private final MeasurementService measurementService;

    /**
     * Receives an image and ROI, sends it to the AI, and returns the measurement value and confidence.
     *
     * @param file   the image file (ROI)
     * @param prompt the prompt for the AI (optional)
     * @return measurement value and confidence
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MeasurementResponse> extractMeasurement(
            @RequestPart("file") MultipartFile file,
            @RequestParam(value = "prompt", required = false, defaultValue = "Extract the measurement value") String prompt
    ) {
        // Demo: returns a static response (replace with OpenAI logic)
        MeasurementResponse response = measurementService.extractMeasurement(file);
        return ResponseEntity.ok(response);
    }

    /**
     * Extracts measurement from an image file located at the given path.
     *
     * @param filePath the path to the image file
     * @return measurement value and confidence
     * @throws IOException if an I/O error occurs
     */
    @GetMapping(value = "/by-path", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MeasurementResponse> extractMeasurementByPath(
            @RequestParam("filePath") String filePath
    ) throws IOException {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(filePath)) {
            if (is == null) {
                return ResponseEntity.notFound().build();
            }

            String base64Image = Base64.getEncoder().encodeToString(is.readAllBytes());
            MeasurementResponse response = measurementService.extractMeasurement(base64Image);
            return ResponseEntity.ok(response);
        }
    }
}
