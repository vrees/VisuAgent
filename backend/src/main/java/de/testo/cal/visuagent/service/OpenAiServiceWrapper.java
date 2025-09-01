package de.testo.cal.visuagent.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.testo.cal.visuagent.model.MeasurementResponse;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Service for communication with OpenAI API.
 */
@Service
@Slf4j
public class OpenAiServiceWrapper {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.model}")
    private String model;

    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";

    @PostConstruct
    void init() {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("OPENAI_API_KEY environment variable is not set");
        }
        log.info("OpenAI service initialized with model: {}", model);
        log.info("API Key starts with: {}...", apiKey != null && apiKey.length() > 10 ? apiKey.substring(0, 10) : "null");
    }

    public MeasurementResponse extractMeasurement(String prompt, String base64Image) {
        try {
            // Prepare headers
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + apiKey);
            headers.set("Content-Type", "application/json");

            // Prepare request body
            Map<String, Object> requestBody = createRequestBody(prompt, base64Image);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            // Call OpenAI API
            ResponseEntity<String> response = restTemplate.exchange(
                    OPENAI_API_URL,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            // Parse response
            JsonNode responseJson = objectMapper.readTree(response.getBody());
            String responseText = extractResponseText(responseJson);

            log.info("OpenAI Response: {}", responseText);

            // Parse the response to extract value and confidence from JSON
            return parseResponse(responseText);

        } catch (Exception e) {
            log.error("Error calling OpenAI API: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to extract measurement from image", e);
        }
    }

    private Map<String, Object> createRequestBody(String prompt, String base64Image) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);  // Use vision-capable model
        requestBody.put("max_tokens", 300);

        // Create messages array
        Map<String, Object> message = new HashMap<>();
        message.put("role", "user");

        // Create content array with text and image
        List<Map<String, Object>> content = List.of(
                Map.of("type", "text", "text", prompt),
                Map.of("type", "image_url", "image_url",
                        Map.of("url", "data:image/jpeg;base64," + base64Image))
        );

        message.put("content", content);
        requestBody.put("messages", List.of(message));

        return requestBody;
    }

    private String extractResponseText(JsonNode responseJson) {
        try {
            return responseJson
                    .path("choices")
                    .get(0)
                    .path("message")
                    .path("content")
                    .asText();
        } catch (Exception e) {
            log.error("Error parsing OpenAI response: {}", e.getMessage());
            throw new RuntimeException("Failed to parse OpenAI response", e);
        }
    }

    private MeasurementResponse parseResponse(String responseText) {
        try {
            // Clean the response text - remove markdown code blocks if present
            String cleanedResponse = responseText;
            if (responseText.contains("```json")) {
                cleanedResponse = responseText.replaceAll("```json\\s*", "").replaceAll("```\\s*", "").trim();
            }
            
            // Try to parse as JSON first
            JsonNode jsonNode = objectMapper.readTree(cleanedResponse);
            
            if (jsonNode.has("value") && jsonNode.has("confidence")) {
                float value = (float) jsonNode.get("value").asDouble();
                float confidence = (float) jsonNode.get("confidence").asDouble();
                
                // Ensure confidence is within valid range
                confidence = Math.clamp(confidence, 0.0f, 1.0f);
                
                log.info("Parsed JSON response: value={}, confidence={}", value, confidence);
                return new MeasurementResponse(value, confidence);
            }
        } catch (Exception e) {
            log.debug("Could not parse response as JSON, trying regex fallback: {}", e.getMessage());
        }

        // Fallback - try to extract just a number if JSON parsing failed
        Pattern numberPattern = Pattern.compile("([+-]?\\d*\\.?\\d+)");
        Matcher numberMatcher = numberPattern.matcher(responseText);

        if (numberMatcher.find()) {
            try {
                float value = Float.parseFloat(numberMatcher.group(1));
                return new MeasurementResponse(value, 0.5f); // Medium confidence for fallback
            } catch (NumberFormatException e) {
                log.warn("Could not parse number from response: {}", numberMatcher.group(1));
            }
        }

        log.warn("Could not extract measurement from response: {}", responseText);
        return new MeasurementResponse(0.0f, 0.0f);
    }
}
