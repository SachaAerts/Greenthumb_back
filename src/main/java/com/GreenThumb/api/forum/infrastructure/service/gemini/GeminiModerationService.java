package com.GreenThumb.api.forum.infrastructure.service.gemini;

import com.GreenThumb.api.forum.infrastructure.config.GeminiApiProperties;
import com.GreenThumb.api.forum.infrastructure.dto.gemini.*;
import com.GreenThumb.api.forum.infrastructure.exception.GeminiApiException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;
import java.util.List;

@Slf4j
@Service
public class GeminiModerationService {
    private final WebClient webClient;
    private final GeminiApiProperties properties;
    private final ObjectMapper objectMapper;
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(10);

    private static final String MODERATION_PROMPT = """
            Tu es un système de modération de contenus pour une plateforme de jardinage francophone nommée GreenThumb.

            Analyse le message suivant et détermine s'il est approprié pour publication sur le forum.

            Critères de modération :
            - REJETÉ si : contenu toxique, insultes, spam, harcèlement, contenu hors-sujet majeur, désinformation dangereuse
            - ACCEPTÉ si : questions de jardinage, partages d'expérience, conseils bienveillants, discussions respectueuses

            Message à analyser : "%s"

            Réponds UNIQUEMENT avec un objet JSON au format suivant :
            {
              "valide": true ou false,
              "raison": "explication concise en français (max 100 caractères)",
              "categorie": "accepte" ou "toxique" ou "spam" ou "hors-sujet" ou "desinformation"
            }
            """;

    public GeminiModerationService(WebClient geminiWebClient, GeminiApiProperties properties) {
        this.webClient = geminiWebClient;
        this.properties = properties;
        this.objectMapper = new ObjectMapper();
    }

    public ModerationResult analyzeContent(String message) {
        log.info("Analyzing content for moderation: message length={}", message != null ? message.length() : 0);

        String prompt = String.format(MODERATION_PROMPT, message);

        GeminiRequest request = new GeminiRequest(
                List.of(new GeminiContent(List.of(new GeminiPart(prompt)))),
                new GeminiGenerationConfig("application/json", properties.getTemperature())
        );

        String fullUrl = properties.getBaseUrl() + "/models/" + properties.getModel() + ":generateContent";
        log.info("Calling Gemini API at: {}", fullUrl);
        log.info("Using model: {}", properties.getModel());

        GeminiResponse response = webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/models/{model}:generateContent")
                        .build(properties.getModel()))
                .header("X-goog-api-key", properties.getKey())
                .bodyValue(request)
                .retrieve()
                .bodyToMono(GeminiResponse.class)
                .timeout(REQUEST_TIMEOUT)
                .doOnSuccess(resp -> {
                    if (resp != null) {
                        log.info("Received response from Gemini API");
                    }
                })
                .onErrorMap(WebClientResponseException.Unauthorized.class, e -> {
                    log.error("Unauthorized: Invalid Google API key - Response: {}", e.getResponseBodyAsString());
                    return new GeminiApiException("Invalid API key", e);
                })
                .onErrorMap(WebClientResponseException.TooManyRequests.class, e -> {
                    log.error("Rate limit exceeded for Gemini API - Response: {}", e.getResponseBodyAsString());
                    return new GeminiApiException("Too many requests, please try again later", e);
                })
                .onErrorMap(WebClientResponseException.class, e -> {
                    log.error("Error calling Gemini API: {} - {} - Response Body: {}",
                              e.getStatusCode(), e.getMessage(), e.getResponseBodyAsString());
                    return new GeminiApiException("Failed to analyze content with Gemini API", e);
                })
                .onErrorMap(e -> !(e instanceof GeminiApiException), e -> {
                    log.error("Unexpected error while analyzing content", e);
                    return new GeminiApiException("Unexpected error occurred", e);
                })
                .block();

        return parseResponse(response);
    }

    private ModerationResult parseResponse(GeminiResponse response) {
        if (response == null || response.getCandidates() == null || response.getCandidates().isEmpty()) {
            throw new GeminiApiException("Empty response from Gemini API");
        }

        GeminiCandidate candidate = response.getCandidates().get(0);
        if (candidate.getContent() == null ||
                candidate.getContent().parts() == null ||
                candidate.getContent().parts().isEmpty()) {
            throw new GeminiApiException("Invalid response structure from Gemini API");
        }

        String jsonText = candidate.getContent().parts().get(0).text();
        log.debug("Gemini response JSON: {}", jsonText);

        try {
            ModerationResult result = objectMapper.readValue(jsonText, ModerationResult.class);
            log.info("Moderation result: valide={}, categorie={}", result.valide(), result.categorie());
            return result;
        } catch (JsonProcessingException e) {
            log.error("Failed to parse moderation result: {}", jsonText, e);
            throw new GeminiApiException("Failed to parse moderation result", e);
        }
    }
}
