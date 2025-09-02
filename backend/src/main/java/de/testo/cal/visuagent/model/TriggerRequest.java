package de.testo.cal.visuagent.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request model for external measurement trigger API.
 * Used by external systems (TestoApp) to trigger measurement extraction.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TriggerRequest {
    
    @NotBlank(message = "Order number is required")
    @Pattern(regexp = "[a-zA-Z0-9]*", message = "Order number must contain only letters and numbers")
    private String orderNumber;
    
    @NotBlank(message = "Equipment number is required") 
    @Pattern(regexp = "[a-zA-Z0-9]*", message = "Equipment number must contain only letters and numbers")
    private String equipmentNumber;
}