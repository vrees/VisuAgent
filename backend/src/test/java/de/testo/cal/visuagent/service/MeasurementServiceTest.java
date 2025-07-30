package de.testo.cal.visuagent.service;

import de.testo.cal.visuagent.model.MeasurementResponse;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for MeasurementService.
 *
 * @author GitHub Copilot
 */
class MeasurementServiceTest {

    @Test
    void extractMeasurement_returnsStaticValue() {
        // OpenAiServiceWrapper mocken
        OpenAiServiceWrapper openAiServiceWrapper = org.mockito.Mockito.mock(OpenAiServiceWrapper.class);
        org.mockito.Mockito.when(openAiServiceWrapper.extractMeasurement(org.mockito.Mockito.anyString(), org.mockito.Mockito.anyString()))
                .thenReturn("23.5 Newton");

        MeasurementService service = new MeasurementService(openAiServiceWrapper);
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", new byte[]{1,2,3});
        MeasurementResponse response = service.extractMeasurement(file, "Extract the measurement value and unit from the image");
        assertEquals("23.5", response.getValue());
        assertEquals("Newton", response.getUnit());
    }
}
