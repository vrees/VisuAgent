package de.testo.cal.visuagent.controller;

import de.testo.cal.visuagent.model.Calibration;
import de.testo.cal.visuagent.model.CalibrationId;
import de.testo.cal.visuagent.service.CalibrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/calibration")
@RequiredArgsConstructor
public class CalibrationController {

    private final CalibrationService calibrationService;

    @PostMapping
    public ResponseEntity<Calibration> createCalibration(@RequestBody Calibration calibration) {
        Calibration created = calibrationService.create(calibration);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<Calibration>> getAllCalibrations() {
        List<Calibration> calibrations = calibrationService.findAll();
        return ResponseEntity.ok(calibrations);
    }

    @GetMapping("/{orderNumber}/{equipmentNumber}")
    public ResponseEntity<Calibration> getCalibrationById(
            @PathVariable String orderNumber,
            @PathVariable String equipmentNumber) {
        return calibrationService.findById(orderNumber, equipmentNumber)
                .map(calibration -> ResponseEntity.ok(calibration))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{orderNumber}/{equipmentNumber}")
    public ResponseEntity<Calibration> updateCalibration(
            @PathVariable String orderNumber,
            @PathVariable String equipmentNumber,
            @RequestBody Calibration calibration) {
        CalibrationId id = new CalibrationId(orderNumber, equipmentNumber);
        calibration.setId(id);

        if (!calibrationService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        Calibration updated = calibrationService.update(calibration);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{orderNumber}/{equipmentNumber}")
    public ResponseEntity<Void> deleteCalibration(
            @PathVariable String orderNumber,
            @PathVariable String equipmentNumber) {
        CalibrationId id = new CalibrationId(orderNumber, equipmentNumber);

        if (!calibrationService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        calibrationService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/exists/{orderNumber}/{equipmentNumber}")
    public ResponseEntity<Boolean> existsCalibration(
            @PathVariable String orderNumber,
            @PathVariable String equipmentNumber) {
        boolean exists = calibrationService.existsById(orderNumber, equipmentNumber);
        return ResponseEntity.ok(exists);
    }
}
