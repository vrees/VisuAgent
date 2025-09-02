package de.testo.cal.visuagent.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response model for external measurement trigger API.
 * Contains status information, session tracking data, and measurement results.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TriggerResponse {
    
    private String status;          // "success", "error", "not_found"
    private String message;         // Human-readable status message
    private String sessionId;       // UUID for frontend tracking
    private LocalDateTime timestamp;
    
    // Measurement result fields
    private Float value;            // Recognized measurement value (null on error)
    private Float confidence;       // AI confidence level 0.0-1.0 (null on error)
    private String roiImageBase64;  // Base64 encoded ROI image (null on error)
    private String orderNumber;     // Order number from trigger
    private String equipmentNumber; // Equipment number from trigger
    
    public static TriggerResponse success(String sessionId, String message) {
        return new TriggerResponse("success", message, sessionId, LocalDateTime.now(), 
                                 null, null, null, null, null);
    }
    
    public static TriggerResponse success(String sessionId, String message, 
                                        ExtendedMeasurementResult result) {
        return new TriggerResponse("success", message, sessionId, LocalDateTime.now(),
                                 result.getValue(), result.getConfidence(), 
                                 result.getRoiImageBase64(), result.getOrderNumber(), 
                                 result.getEquipmentNumber());
    }
    
    public static TriggerResponse error(String message) {
        return new TriggerResponse("error", message, null, LocalDateTime.now(),
                                 null, null, null, null, null);
    }
    
    public static TriggerResponse error(String message, String orderNumber, String equipmentNumber) {
        return new TriggerResponse("error", message, null, LocalDateTime.now(),
                                 null, null, null, orderNumber, equipmentNumber);
    }
    
    public static TriggerResponse notFound(String message) {
        return new TriggerResponse("not_found", message, null, LocalDateTime.now(),
                                 null, null, null, null, null);
    }
    
    public static TriggerResponse notFound(String message, String orderNumber, String equipmentNumber) {
        return new TriggerResponse("not_found", message, null, LocalDateTime.now(),
                                 null, null, null, orderNumber, equipmentNumber);
    }
}
