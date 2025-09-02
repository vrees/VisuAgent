package de.testo.cal.visuagent.service;

import de.testo.cal.visuagent.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

/**
 * Orchestrates the external measurement trigger workflow.
 * Coordinates calibration lookup, image processing, and AI measurement.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MeasurementTriggerService {
    
    private final CalibrationService calibrationService;
    private final ImageStreamService imageStreamService;
    private final MeasurementService measurementService;
    
    /**
     * Triggers measurement extraction based on external request.
     * 
     * @param request Trigger request with order and equipment numbers
     * @return Extended measurement result for frontend display
     * @throws CalibrationNotFoundException if calibration data not found
     * @throws MeasurementException if measurement extraction fails
     */
    public ExtendedMeasurementResult triggerMeasurement(TriggerRequest request) 
            throws CalibrationNotFoundException, MeasurementException {
        
        String sessionId = UUID.randomUUID().toString();
        log.info("Starting measurement trigger for order: {}, equipment: {}, session: {}", 
                request.getOrderNumber(), request.getEquipmentNumber(), sessionId);
        
        // Step 1: Look up calibration data
        Optional<Calibration> calibrationOpt = calibrationService.findById(
            request.getOrderNumber(), request.getEquipmentNumber());
            
        if (calibrationOpt.isEmpty()) {
            throw new CalibrationNotFoundException(
                "Calibration not found for order: " + request.getOrderNumber() + 
                ", equipment: " + request.getEquipmentNumber());
        }
        
        Calibration calibration = calibrationOpt.get();
        ImageArea imageArea = calibration.getImageArea();
        
        if (imageArea == null) {
            throw new MeasurementException(
                "No image area defined in calibration for order: " + request.getOrderNumber());
        }
        
        log.debug("Found calibration with ROI: x={}, y={}, width={}, height={}", 
                 imageArea.getX(), imageArea.getY(), imageArea.getWidth(), imageArea.getHeight());
        
        // Step 2: Extract ROI from current stream
        String roiImageBase64 = imageStreamService.extractROIFromCurrentStream(imageArea);
        if (roiImageBase64 == null) {
            throw new MeasurementException("Failed to extract ROI from current stream");
        }
        
        log.debug("Successfully extracted ROI image ({} bytes)", roiImageBase64.length());
        
        // Step 3: Perform AI measurement
        MeasurementResponse measurementResponse = measurementService.extractMeasurement(roiImageBase64);
        if (measurementResponse == null) {
            throw new MeasurementException("AI measurement extraction failed");
        }
        
        log.info("Measurement completed: value={}, confidence={}", 
                measurementResponse.value, measurementResponse.confidence);
        
        // Step 4: Create extended result for frontend
        ExtendedMeasurementResult result = ExtendedMeasurementResult.fromMeasurementResponse(
            measurementResponse,
            "data:image/jpeg;base64," + roiImageBase64,
            request.getOrderNumber(),
            request.getEquipmentNumber(),
            sessionId,
            "external"
        );
        
        log.info("External measurement trigger completed successfully for session: {}", sessionId);
        return result;
    }
    
    /**
     * Custom exception for calibration not found scenarios.
     */
    public static class CalibrationNotFoundException extends Exception {
        public CalibrationNotFoundException(String message) {
            super(message);
        }
    }
    
    /**
     * Custom exception for measurement processing failures.
     */
    public static class MeasurementException extends Exception {
        public MeasurementException(String message) {
            super(message);
        }
    }
}