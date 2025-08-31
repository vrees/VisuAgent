package de.testo.cal.visuagent.service;

import de.testo.cal.visuagent.model.Calibration;
import de.testo.cal.visuagent.model.CalibrationId;
import de.testo.cal.visuagent.repository.CalibrationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CalibrationService {

    private final CalibrationRepository calibrationRepository;

    public Calibration create(Calibration calibration) {
        return calibrationRepository.save(calibration);
    }

    public List<Calibration> findAll() {
        return calibrationRepository.findAll();
    }

    public Optional<Calibration> findById(CalibrationId id) {
        return calibrationRepository.findById(id);
    }

    public Optional<Calibration> findById(String orderNumber, String equipmentNumber) {
        CalibrationId id = new CalibrationId(orderNumber, equipmentNumber);
        return calibrationRepository.findById(id);
    }

    public Calibration update(Calibration calibration) {
        return calibrationRepository.save(calibration);
    }

    public void deleteById(CalibrationId id) {
        calibrationRepository.deleteById(id);
    }

    public void deleteById(String orderNumber, String equipmentNumber) {
        CalibrationId id = new CalibrationId(orderNumber, equipmentNumber);
        calibrationRepository.deleteById(id);
    }

    public boolean existsById(CalibrationId id) {
        return calibrationRepository.existsById(id);
    }

    public boolean existsById(String orderNumber, String equipmentNumber) {
        CalibrationId id = new CalibrationId(orderNumber, equipmentNumber);
        return calibrationRepository.existsById(id);
    }
}