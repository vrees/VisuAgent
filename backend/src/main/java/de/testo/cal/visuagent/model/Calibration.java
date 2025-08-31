package de.testo.cal.visuagent.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "calibration")
public class Calibration {

    @EmbeddedId
    private CalibrationId id;

    @Column(name = "remark", length = 1000)
    private String remark;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "x", column = @Column(name = "image_x")),
            @AttributeOverride(name = "y", column = @Column(name = "image_y")),
            @AttributeOverride(name = "width", column = @Column(name = "image_width")),
            @AttributeOverride(name = "height", column = @Column(name = "image_height"))
    })
    private ImageArea imageArea;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
