package de.testo.cal.visuagent.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit test for StreamController.
 *
 * @author GitHub Copilot
 */
@WebMvcTest(StreamController.class)
class StreamControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getStream_returnsOk() throws Exception {
        mockMvc.perform(get("/api/stream"))
                .andExpect(status().isOk());
    }
}
