package de.testo.cal.visuagent.service;

import de.testo.cal.visuagent.model.ExtendedMeasurementResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * Service for broadcasting messages to frontend via WebSocket.
 * Handles real-time communication for measurement results and system status.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WebSocketMessagingService {
    
    private final SimpMessagingTemplate messagingTemplate;
    
    // Topic destinations
    private static final String MEASUREMENT_TOPIC = "/topic/measurement";
    private static final String STATUS_TOPIC = "/topic/status";
    
    /**
     * Broadcasts measurement result to all connected frontend clients.
     * 
     * @param result The measurement result to broadcast
     */
    public void broadcastMeasurementResult(ExtendedMeasurementResult result) {
        try {
            log.info("Broadcasting measurement result to frontend clients: sessionId={}, value={}, confidence={}", 
                    result.getSessionId(), result.getValue(), result.getConfidence());
            
            messagingTemplate.convertAndSend(MEASUREMENT_TOPIC, result);
            
            log.debug("Successfully broadcasted measurement result for session: {}", result.getSessionId());
            
        } catch (Exception e) {
            log.error("Failed to broadcast measurement result: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Broadcasts status message to frontend clients.
     * 
     * @param status Status type (e.g., "processing", "error", "success")
     * @param message Human-readable status message
     * @param sessionId Optional session ID for tracking
     */
    public void broadcastStatusMessage(String status, String message, String sessionId) {
        try {
            StatusMessage statusMessage = new StatusMessage(status, message, sessionId);
            
            log.info("Broadcasting status message: status={}, sessionId={}, message={}", 
                    status, sessionId, message);
            
            messagingTemplate.convertAndSend(STATUS_TOPIC, statusMessage);
            
        } catch (Exception e) {
            log.error("Failed to broadcast status message: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Broadcasts status message without session ID.
     */
    public void broadcastStatusMessage(String status, String message) {
        broadcastStatusMessage(status, message, null);
    }
    
    /**
     * Sends measurement result to a specific user session.
     * 
     * @param sessionId Target session ID
     * @param result Measurement result to send
     */
    public void sendMeasurementResultToUser(String sessionId, ExtendedMeasurementResult result) {
        try {
            String destination = "/queue/measurement/" + sessionId;
            
            log.info("Sending measurement result to specific session: {}", sessionId);
            
            messagingTemplate.convertAndSend(destination, result);
            
        } catch (Exception e) {
            log.error("Failed to send measurement result to session {}: {}", sessionId, e.getMessage(), e);
        }
    }
    
    /**
     * Data class for status messages.
     */
    public static class StatusMessage {
        public final String status;
        public final String message;
        public final String sessionId;
        public final long timestamp;
        
        public StatusMessage(String status, String message, String sessionId) {
            this.status = status;
            this.message = message;
            this.sessionId = sessionId;
            this.timestamp = System.currentTimeMillis();
        }
    }
}