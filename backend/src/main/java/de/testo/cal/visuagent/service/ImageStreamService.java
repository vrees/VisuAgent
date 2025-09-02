package de.testo.cal.visuagent.service;

import de.testo.cal.visuagent.model.ImageArea;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Base64;

/**
 * Service for accessing and processing camera stream images.
 * Provides functionality to extract ROI regions from live camera feed.
 */
@Service
@Slf4j
public class ImageStreamService {
    
    private static final String STREAM_URL = "http://localhost:8082/api/stream";
    
    /**
     * Gets the current image from the camera stream.
     * 
     * @return Current camera image or null if not available
     */
    public BufferedImage getCurrentStreamImage() {
        try {
            URL url = new URL(STREAM_URL + "?t=" + System.currentTimeMillis());
            BufferedImage image = ImageIO.read(url);
            log.debug("Successfully retrieved current stream image: {}x{}", 
                     image != null ? image.getWidth() : 0, 
                     image != null ? image.getHeight() : 0);
            return image;
        } catch (IOException e) {
            log.error("Failed to retrieve current stream image: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * Extracts a Region of Interest (ROI) from the given image.
     * 
     * @param sourceImage The source image
     * @param roi The region of interest coordinates
     * @return Extracted ROI image or null if extraction fails
     */
    public BufferedImage extractROI(BufferedImage sourceImage, ImageArea roi) {
        if (sourceImage == null || roi == null) {
            log.warn("Cannot extract ROI: sourceImage or roi is null");
            return null;
        }
        
        // Validate ROI coordinates
        if (roi.getX() < 0 || roi.getY() < 0 || 
            roi.getWidth() <= 0 || roi.getHeight() <= 0) {
            log.warn("Invalid ROI coordinates: x={}, y={}, width={}, height={}", 
                    roi.getX(), roi.getY(), roi.getWidth(), roi.getHeight());
            return null;
        }
        
        // Check if ROI is within image bounds
        if (roi.getX() + roi.getWidth() > sourceImage.getWidth() || 
            roi.getY() + roi.getHeight() > sourceImage.getHeight()) {
            log.warn("ROI extends beyond image bounds. Image: {}x{}, ROI: ({},{}) {}x{}", 
                    sourceImage.getWidth(), sourceImage.getHeight(),
                    roi.getX(), roi.getY(), roi.getWidth(), roi.getHeight());
            return null;
        }
        
        try {
            BufferedImage roiImage = sourceImage.getSubimage(
                roi.getX(), roi.getY(), roi.getWidth(), roi.getHeight());
            
            log.debug("Successfully extracted ROI: {}x{} from position ({},{})", 
                     roi.getWidth(), roi.getHeight(), roi.getX(), roi.getY());
            
            return roiImage;
        } catch (Exception e) {
            log.error("Failed to extract ROI: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * Converts a BufferedImage to Base64 encoded string.
     * 
     * @param image The image to convert
     * @return Base64 encoded image string or null if conversion fails
     */
    public String imageToBase64(BufferedImage image) {
        if (image == null) {
            return null;
        }
        
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "jpg", baos);
            byte[] imageBytes = baos.toByteArray();
            return Base64.getEncoder().encodeToString(imageBytes);
        } catch (IOException e) {
            log.error("Failed to convert image to Base64: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * Combines getCurrentStreamImage, extractROI and imageToBase64 in one operation.
     * 
     * @param roi The region of interest to extract
     * @return Base64 encoded ROI image or null if any step fails
     */
    public String extractROIFromCurrentStream(ImageArea roi) {
        BufferedImage currentImage = getCurrentStreamImage();
        if (currentImage == null) {
            return null;
        }
        
        BufferedImage roiImage = extractROI(currentImage, roi);
        if (roiImage == null) {
            return null;
        }
        
        return imageToBase64(roiImage);
    }
}