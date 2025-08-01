package de.testo.cal.visuagent.service;


import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;

import com.openai.models.ChatModel;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.responses.Response;
import com.openai.models.responses.ResponseCreateParams;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service for communication with OpenAI API.
 *
 * @author GitHub Copilot
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class OpenAiServiceWrapper {

    // Configures using the `OPENAI_API_KEY`, `OPENAI_ORG_ID` and `OPENAI_PROJECT_ID`
    // environment variables
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


    public String extractMeasurement(String prompt, String base64Image) {


        ResponseCreateParams params = ResponseCreateParams.builder()
                .input(prompt)
                .model(ChatModel.GPT_4_1)
                .build();


        try {
            Response response = client.responses().create(params);

            log.info(response.toString());

            return response.toString();
        } catch (Exception e) {
            log.error("Error while creating completion with OpenAI service", e);
            throw e; // Optional: Weiterwerfen der Ausnahme, falls erforderlich
        }

    }
}
