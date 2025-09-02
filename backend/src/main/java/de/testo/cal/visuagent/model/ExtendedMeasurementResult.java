package de.testo.cal.visuagent.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Extended measurement result model that includes additional metadata
 * for external trigger functionality.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExtendedMeasurementResult {
    
    private float value;                    // Recognized measurement value
    private float confidence;               // AI confidence level (0.0-1.0)
    private String roiImageBase64;          // Base64 encoded ROI image
    private String triggerSource;           // "manual" or "external"
    private String orderNumber;             // Order number from trigger
    private String equipmentNumber;         // Equipment number from trigger
    private String sessionId;               // Session tracking ID
    private LocalDateTime timestamp;        // Measurement timestamp
    
    /**
     * Create ExtendedMeasurementResult from basic MeasurementResponse
     */
    public static ExtendedMeasurementResult fromMeasurementResponse(
            MeasurementResponse response, 
            String roiImageBase64,
            String orderNumber, 
            String equipmentNumber,
            String sessionId,
            String triggerSource) {
        
        return new ExtendedMeasurementResult(
            response.value,
            response.confidence,
            roiImageBase64,
            triggerSource,
            orderNumber,
            equipmentNumber,
            sessionId,
            LocalDateTime.now()
        );
    }
}