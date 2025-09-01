package de.testo.cal.visuagent.controller;

import de.testo.cal.visuagent.service.WebSocketMessagingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

/**
 * WebSocket controller for handling real-time messaging between frontend and backend.
 * Manages WebSocket connections and message routing.
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class WebSocketController {
    
    private final WebSocketMessagingService messagingService;
    
    /**
     * Handles client connection and subscription to measurement updates.
     */
    @MessageMapping("/measurement/subscribe")
    @SendTo("/topic/measurement")
    public String subscribeMeasurement(SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        log.info("Client subscribed to measurement updates: sessionId={}", sessionId);
        
        return "Subscribed to measurement updates";
    }
    
    /**
     * Handles client connection status updates.
     */
    @MessageMapping("/status/subscribe") 
    @SendTo("/topic/status")
    public String subscribeStatus(SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        log.info("Client subscribed to status updates: sessionId={}", sessionId);
        
        return "Subscribed to status updates";
    }
    
    /**
     * Handles client ping messages for connection keep-alive.
     */
    @MessageMapping("/ping")
    @SendTo("/topic/status")
    public WebSocketMessagingService.StatusMessage ping(@Payload String message, 
                                                       SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        log.debug("Ping received from client: sessionId={}, message={}", sessionId, message);
        
        return new WebSocketMessagingService.StatusMessage("pong", "Connection alive", sessionId);
    }
}