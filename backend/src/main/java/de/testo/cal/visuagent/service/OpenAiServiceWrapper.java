package de.testo.cal.visuagent.service;

import com.theokanning.openai.service.OpenAiService;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.completion.CompletionResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service for communication with OpenAI API.
 *
 * @author GitHub Copilot
 */
@Service
public class OpenAiServiceWrapper {

    private final OpenAiService openAiService;

    public OpenAiServiceWrapper(@Value("${openai.api.key}") String apiKey) {
        this.openAiService = new OpenAiService(apiKey);
    }

    /**
     * Calls OpenAI API with the given prompt and image (Base64 encoded).
     *
     * @param prompt the prompt
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
