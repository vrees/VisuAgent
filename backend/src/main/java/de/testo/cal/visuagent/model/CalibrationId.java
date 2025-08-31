package de.testo.cal.visuagent.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class CalibrationId implements Serializable {
    
    @Column(name = "order_number", length = 20)
    private String orderNumber;
    
    @Column(name = "equipment_number", length = 20) 
    private String equipmentNumber;
}