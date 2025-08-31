package de.testo.cal.visuagent.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class ImageArea {
    private int x;
    private int y;
    private int width;
    private int height;
}