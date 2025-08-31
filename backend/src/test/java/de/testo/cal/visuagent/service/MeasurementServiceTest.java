package de.testo.cal.visuagent.service;

import de.testo.cal.visuagent.model.MeasurementResponse;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.*;

class MeasurementServiceTest {

    @Test
    void extractMeasurement_returnsStaticValue() {
        // OpenAiServiceWrapper mocken
        OpenAiServiceWrapper openAiServiceWrapper = org.mockito.Mockito.mock(OpenAiServiceWrapper.class);
        MeasurementResponse mockResponse = new MeasurementResponse(23.5f, 0.95f);
        org.mockito.Mockito.when(openAiServiceWrapper.extractMeasurement(org.mockito.Mockito.anyString(), org.mockito.Mockito.anyString()))
                .thenReturn(mockResponse);

        MeasurementService service = new MeasurementService(openAiServiceWrapper);
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", new byte[]{1, 2, 3});
        MeasurementResponse response = service.extractMeasurement(file);
        assertEquals(23.5, response.value);
        assertEquals(0.95f, response.confidence);
    }
}
