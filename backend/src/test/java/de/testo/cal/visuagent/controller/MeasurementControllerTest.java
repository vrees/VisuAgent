package de.testo.cal.visuagent.controller;

import de.testo.cal.visuagent.model.MeasurementResponse;
import de.testo.cal.visuagent.service.MeasurementService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit test for MeasurementController.
 *
 * @author GitHub Copilot
 */
@WebMvcTest(MeasurementController.class)
class MeasurementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MeasurementService measurementService;

    @Test
    void extractMeasurement_returnsValueAndUnit() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", MediaType.IMAGE_JPEG_VALUE, new byte[]{1,2,3});
        Mockito.when(measurementService.extractMeasurement(Mockito.any(), Mockito.anyString()))
                .thenReturn(new MeasurementResponse("12.3", "Volt"));

        mockMvc.perform(multipart("/api/measurements")
                        .file(file)
                        .param("prompt", "Extract the measurement value and unit from the image"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.value").value("12.3"))
                .andExpect(jsonPath("$.unit").value("Volt"));
    }
}
