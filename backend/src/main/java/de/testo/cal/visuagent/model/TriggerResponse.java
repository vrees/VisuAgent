package de.testo.cal.visuagent.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response model for external measurement trigger API.
 * Contains status information and session tracking data.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TriggerResponse {
    
    private String status;          // "success", "error", "not_found"
    private String message;         // Human-readable status message
    private String sessionId;       // UUID for frontend tracking
    private LocalDateTime timestamp;
    
    public static TriggerResponse success(String sessionId, String message) {
        return new TriggerResponse("success", message, sessionId, LocalDateTime.now());
    }
    
    public static TriggerResponse error(String message) {
        return new TriggerResponse("error", message, null, LocalDateTime.now());
    }
    
    public static TriggerResponse notFound(String message) {
        return new TriggerResponse("not_found", message, null, LocalDateTime.now());
    }
}