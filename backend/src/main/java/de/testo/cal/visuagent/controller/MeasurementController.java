package de.testo.cal.visuagent.controller;

import de.testo.cal.visuagent.model.MeasurementRequest;
import de.testo.cal.visuagent.model.MeasurementResponse;
import de.testo.cal.visuagent.service.MeasurementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * REST controller for measurement extraction endpoint.
 *
 * @author GitHub Copilot
 */
@RestController
@RequestMapping("/api/measurements")
public class MeasurementController {

    private final MeasurementService measurementService;

    @Autowired
    public MeasurementController(MeasurementService measurementService) {
        this.measurementService = measurementService;
    }

    /**
     * Receives an image and ROI, sends it to the AI, and returns the measurement value and unit.
     *
     * @param file the image file (ROI)
     * @param prompt the prompt for the AI (optional)
     * @return measurement value and unit
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MeasurementResponse> extractMeasurement(
            @RequestPart("file") MultipartFile file,
            @RequestParam(value = "prompt", required = false, defaultValue = "Extract the measurement value and unit from the image") String prompt
    ) {
        // Demo: returns a static response (replace with OpenAI logic)
        MeasurementResponse response = measurementService.extractMeasurement(file, prompt);
        return ResponseEntity.ok(response);
    }
}
