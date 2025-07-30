package de.testo.cal.visuagent.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * REST controller for video stream endpoint.
 *
 * @author GitHub Copilot
 */
@RestController
@RequestMapping("/api")
public class StreamController {

    /**
     * Returns a demo video stream (placeholder).
     * In production, this should stream from the connected camera.
     *
     * @return video stream as ResponseEntity
     */
    @GetMapping(value = "/stream", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<Resource> getStream() throws IOException {
        // Demo: returns a static video file from resources (replace with camera stream logic)
        Resource video = new ClassPathResource("demo/demo-video.mp4");
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).body(video);
    }
}
