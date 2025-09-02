package de.testo.cal.visuagent.controller;

import de.testo.cal.visuagent.model.ExtendedMeasurementResult;
import de.testo.cal.visuagent.model.TriggerRequest;
import de.testo.cal.visuagent.model.TriggerResponse;
import de.testo.cal.visuagent.service.MeasurementTriggerService;
import de.testo.cal.visuagent.service.MeasurementTriggerService.CalibrationNotFoundException;
import de.testo.cal.visuagent.service.MeasurementTriggerService.MeasurementException;
import de.testo.cal.visuagent.service.WebSocketMessagingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for external measurement trigger API.
 * Allows external systems (TestoApp) to trigger measurement extractions.
 */
@RestController
@RequestMapping("/api/trigger")
@RequiredArgsConstructor
@Slf4j
public class TriggerController {
    
    private final MeasurementTriggerService measurementTriggerService;
    private final WebSocketMessagingService webSocketMessagingService;
    
    @Operation(summary = "Trigger measurement extraction from external system",
               description = "Triggers AI-based measurement extraction using stored calibration data")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Measurement triggered successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
        @ApiResponse(responseCode = "404", description = "Calibration not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error during measurement")
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TriggerResponse> triggerMeasurement(@Valid @RequestBody TriggerRequest request) {
        
        log.info("External trigger request received: orderNumber={}, equipmentNumber={}", 
                request.getOrderNumber(), request.getEquipmentNumber());
        
        try {
            // Broadcast processing status to frontend
            webSocketMessagingService.broadcastStatusMessage("processing", 
                "Measurement processing started for order: " + request.getOrderNumber());
            
            // Trigger the measurement process
            ExtendedMeasurementResult result = measurementTriggerService.triggerMeasurement(request);
            
            // Broadcast result to frontend via WebSocket
            webSocketMessagingService.broadcastMeasurementResult(result);
            
            // Also broadcast success status
            webSocketMessagingService.broadcastStatusMessage("success", 
                "Measurement completed successfully", result.getSessionId());
            
            log.info("Measurement result broadcasted to frontend: sessionId={}, value={}, confidence={}", 
                    result.getSessionId(), result.getValue(), result.getConfidence());
            
            TriggerResponse response = TriggerResponse.success(
                result.getSessionId(),
                "Measurement triggered and result sent to frontend",
                result
            );
            
            return ResponseEntity.ok(response);
            
        } catch (CalibrationNotFoundException e) {
            log.warn("Calibration not found: {}", e.getMessage());
            webSocketMessagingService.broadcastStatusMessage("error", 
                "Calibration not found: " + e.getMessage());
            TriggerResponse response = TriggerResponse.notFound(e.getMessage(), 
                request.getOrderNumber(), request.getEquipmentNumber());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            
        } catch (MeasurementException e) {
            log.error("Measurement processing failed: {}", e.getMessage());
            webSocketMessagingService.broadcastStatusMessage("error", 
                "Measurement processing failed: " + e.getMessage());
            TriggerResponse response = TriggerResponse.error(
                "Measurement processing failed: " + e.getMessage(),
                request.getOrderNumber(), request.getEquipmentNumber());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            
        } catch (Exception e) {
            log.error("Unexpected error during measurement trigger: {}", e.getMessage(), e);
            webSocketMessagingService.broadcastStatusMessage("error", 
                "Internal server error occurred");
            TriggerResponse response = TriggerResponse.error(
                "Internal server error occurred",
                request.getOrderNumber(), request.getEquipmentNumber());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @Operation(summary = "Health check for trigger API")
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Trigger API is operational");
    }
}