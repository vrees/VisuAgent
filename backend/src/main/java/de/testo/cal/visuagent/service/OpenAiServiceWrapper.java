package de.testo.cal.visuagent.service;

import com.theokanning.openai.service.OpenAiService;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.completion.CompletionResult;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service for communication with OpenAI API.
 *
 * @author GitHub Copilot
 */
@RequiredArgsConstructor
@Service
public class OpenAiServiceWrapper {

    private OpenAiService openAiService;

    @Value("${openai.api.key}")
    String apiKey;

    @PostConstruct
    void init() {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("OPENAI_API_KEY environment variable is not set");
        }
        this.openAiService = new OpenAiService(apiKey);
    }

    /**
     * Calls OpenAI API with the given prompt and image (Base64 encoded).
     *
     * @param prompt      the prompt
     * @param base64Image the image as Base64 string
     * @return result from OpenAI
     */
    public String extractMeasurement(String prompt, String base64Image) {
        // Demo: Only text completion, no real vision API call
        CompletionRequest request = CompletionRequest.builder()
                .prompt(prompt + " (image omitted)")
                .model("text-davinci-003")
                .maxTokens(20)
                .build();
        CompletionResult result = openAiService.createCompletion(request);
        return result.getChoices().get(0).getText();
    }
}
