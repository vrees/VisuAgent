package de.testo.cal.visuagent.controller;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * REST controller for video stream endpoint.
 * Provides single JPEG frames from USB camera or fallback images.
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200")
@Slf4j
public class StreamController {

    private volatile boolean cameraAvailable = false;
    private volatile boolean cameraInitialized = false;
    private volatile BufferedImage lastCapturedFrame = null;
    private final ScheduledExecutorService cameraScheduler = Executors.newScheduledThreadPool(1);
    private Process cameraProcess = null;

    /**
     * Returns a single camera frame as JPEG image.
     * Each request generates a fresh frame.
     *
     * @return JPEG image as ResponseEntity
     */
    @GetMapping(value = "/stream", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getStream() {
        try {
            // Initialize camera only once
            if (!cameraInitialized) {
                initializeCamera();
                startCameraCapture();
                cameraInitialized = true;
            }

            // Return the latest captured frame
            BufferedImage frame = getLatestFrame();

            if (frame != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(frame, "jpg", baos);
                byte[] imageBytes = baos.toByteArray();

                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(imageBytes);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Error generating frame: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    private BufferedImage getLatestFrame() {
        if (cameraAvailable && lastCapturedFrame != null) {
            return lastCapturedFrame;
        } else {
            return generateFallbackFrame();
        }
    }

    private void startCameraCapture() {
        if (cameraAvailable) {
            // Start continuous capture every 2 seconds (slower to avoid conflicts)
            cameraScheduler.scheduleAtFixedRate(this::captureFrameAsync, 0, 2000, TimeUnit.MILLISECONDS);
            log.info("Started continuous camera capture at 0.5 FPS");
        }
    }

    private void captureFrameAsync() {
        try {
            String filename = "/tmp/camera_frame_stream.jpg";

            // Kill previous process if still running
            if (cameraProcess != null && cameraProcess.isAlive()) {
                cameraProcess.destroyForcibly();
            }

            // Use ffmpeg to capture a single frame from camera with increased timeout
            ProcessBuilder pb = new ProcessBuilder(
                    "ffmpeg", "-f", "v4l2", "-i", "/dev/video0",
                    "-vframes", "1", "-f", "image2", "-vcodec", "mjpeg", 
                    "-timeout", "3000000", "-y", filename  // 3 second timeout for input
            );
            pb.redirectErrorStream(true);
            cameraProcess = pb.start();

            // Wait for completion with longer timeout (5 seconds for slow camera)
            boolean finished = cameraProcess.waitFor(5, TimeUnit.SECONDS);

            if (finished && cameraProcess.exitValue() == 0) {
                // Read captured frame
                Path frameFile = Path.of(filename);
                if (Files.exists(frameFile)) {
                    lastCapturedFrame = ImageIO.read(frameFile.toFile());
                }
            } else {
                log.warn("Camera capture timed out or failed");
                if (cameraProcess.isAlive()) {
                    cameraProcess.destroyForcibly();
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Camera capture interrupted: {}", e.getMessage());
            cameraAvailable = false;
        } catch (Exception e) {
            log.error("Async camera capture failed: {}", e.getMessage());
            cameraAvailable = false;
        }
    }

    private void initializeCamera() {
        try {
            // Check if /dev/video0 exists and is accessible
            Path videoDevice = Path.of("/dev/video0");
            if (!Files.exists(videoDevice)) {
                log.warn("No camera device found at /dev/video0");
                cameraAvailable = false;
                return;
            }
            
            // Try to detect available cameras using v4l2-ctl
            ProcessBuilder pb = new ProcessBuilder("v4l2-ctl", "--list-devices");
            pb.redirectErrorStream(true);
            Process process = pb.start();
            boolean finished = process.waitFor(2, TimeUnit.SECONDS);
            
            if (finished && process.exitValue() == 0) {
                cameraAvailable = true;
                log.info("USB camera detected: JOYACCESS camera found at /dev/video0");
            } else {
                log.warn("Camera detection timed out or failed, using fallback");
                cameraAvailable = false;
                if (process.isAlive()) {
                    process.destroyForcibly();
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Camera detection interrupted: {}, using fallback", e.getMessage());
            cameraAvailable = false;
        } catch (Exception e) {
            log.warn("Camera detection failed: {}, using fallback", e.getMessage());
            cameraAvailable = false;
        }
    }

    @PreDestroy
    public void cleanup() {
        log.info("Cleaning up camera resources");
        cameraScheduler.shutdown();
        if (cameraProcess != null && cameraProcess.isAlive()) {
            cameraProcess.destroyForcibly();
        }
    }

    private BufferedImage generateFallbackFrame() {
        try {
            // Generate a simple test pattern with current time
            BufferedImage image = new BufferedImage(640, 480, BufferedImage.TYPE_INT_RGB);
            java.awt.Graphics2D g2d = image.createGraphics();

            // Background
            g2d.setColor(java.awt.Color.DARK_GRAY);
            g2d.fillRect(0, 0, 640, 480);

            // Text
            g2d.setColor(java.awt.Color.WHITE);
            g2d.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 24));
            g2d.drawString("No Camera - Demo Mode", 160, 220);

            // Current time for visual feedback
            String timeStr = "Time: " + LocalTime.now().format(
                    java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));
            g2d.drawString(timeStr, 220, 260);

            // Some visual elements to show it's updating
            g2d.setColor(java.awt.Color.YELLOW);
            int second = LocalTime.now().getSecond();
            g2d.fillOval(300 + (second % 40), 300, 20, 20);

            g2d.dispose();
            return image;
        } catch (Exception e) {
            log.error("Fallback frame generation failed: {}", e.getMessage());
            return null;
        }
    }
}
