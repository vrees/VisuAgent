package de.testo.cal.visuagent.repository;

import de.testo.cal.visuagent.model.Calibration;
import de.testo.cal.visuagent.model.CalibrationId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CalibrationRepository extends JpaRepository<Calibration, CalibrationId> {
}