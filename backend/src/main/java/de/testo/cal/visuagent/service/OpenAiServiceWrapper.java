package de.testo.cal.visuagent.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.ChatModel;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import com.openai.models.chat.completions.StructuredChatCompletionCreateParams;
import com.openai.models.responses.*;
import de.testo.cal.visuagent.model.MeasurementResponse;
import de.testo.cal.visuagent.model.MeasurementResult;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.List.of;

/**
 * Service for communication with OpenAI API.
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class OpenAiServiceWrapper {

    OpenAIClient client = OpenAIOkHttpClient.fromEnv();

    @Value("${openai.api.key}")
    String apiKey;

    @Value("${openai.model}")
    String model;

    @Value("${openai.api.url}")
    String apiUrl;

    @PostConstruct
    void init() {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("OPENAI_API_KEY environment variable is not set");
        }
        if (apiUrl == null || apiUrl.isBlank()) {
            throw new IllegalStateException("OPENAI_API_URL environment variable is not set");
        }
    }

    public MeasurementResponse extractMeasurement(String prompt, String base64Image) {

        String logoBase64Url = "data:image/jpeg;base64," + base64Image;

        ResponseInputImage logoInputImage = ResponseInputImage.builder()
                .detail(ResponseInputImage.Detail.AUTO)
                .imageUrl(logoBase64Url)
                .build();

        ResponseInputItem messageInputItem = ResponseInputItem.ofMessage(ResponseInputItem.Message.builder()
                .role(ResponseInputItem.Message.Role.USER)
                .addInputTextContent(prompt)
                .addContent(logoInputImage)
                .build());

        StructuredChatCompletionCreateParams<MeasurementResult> createParams = ChatCompletionCreateParams.builder()
                .model(ChatModel.GPT_4O_MINI)
                .maxCompletionTokens(2048)
                .responseFormat(MeasurementResult.class)
                .addUserMessage("List some famous late twentieth century novels.")
                .build();
        log.info(createParams.toString());

        // Create a structured response with the input image and prompt
        ResponseCreateParams params = ResponseCreateParams.builder()
                .inputOfResponse(of(messageInputItem))
                .model(ChatModel.GPT_4_1_MINI)
                .store(false)
                .build();

        try {
            Response response = client.responses().create(params);

            StringBuilder jsonBuilder = new StringBuilder();

            // Collect the output text from the response
            response.output().stream()
                    .flatMap(item -> item.message().stream())
                    .flatMap(message -> message.content().stream())
                    .forEach(content -> jsonBuilder.append(content.outputText().get().text()));

            String json = jsonBuilder.toString();

            log.info("Antwort-Rohtext:\n" + json);

            // JSON in POJO parsen
            ObjectMapper mapper = new ObjectMapper();
            MeasurementResponse result = mapper.readValue(json, MeasurementResponse.class);

            return result;
        } catch (JsonProcessingException e) {
            log.error("Error parsing JSON response", e);
            throw new RuntimeException("Failed to parse JSON response", e);
        }
    }
}
