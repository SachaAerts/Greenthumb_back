package com.GreenThumb.api.apigateway.controller;

import com.GreenThumb.api.infrastructure.config.GeminiApiProperties;
import com.GreenThumb.api.infrastructure.dto.gemini.ModerationResult;
import com.GreenThumb.api.infrastructure.service.GeminiModerationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/test/moderation")
public class ModerationTestController {

    private final GeminiModerationService moderationService;
    private final GeminiApiProperties geminiApiProperties;

    public ModerationTestController(GeminiModerationService moderationService, GeminiApiProperties geminiApiProperties) {
        this.moderationService = moderationService;
        this.geminiApiProperties = geminiApiProperties;
        log.info("ModerationTestController initialized");
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        log.info("Health check called");
        boolean apiKeyConfigured = geminiApiProperties.getKey() != null && !geminiApiProperties.getKey().isEmpty();
        String apiKeyStatus = apiKeyConfigured ? "Configured (length: " + geminiApiProperties.getKey().length() + ")" : "NOT CONFIGURED";

        return ResponseEntity.ok(Map.of(
            "status", "OK",
            "message", "Moderation test endpoint is accessible",
            "apiKeyStatus", apiKeyStatus,
            "model", geminiApiProperties.getModel() != null ? geminiApiProperties.getModel() : "NOT SET"
        ));
    }

    @PostMapping
    public ResponseEntity<ModerationResult> testModeration(@RequestBody Map<String, String> request) {
        log.info("POST /api/test/moderation called");
        String message = request.get("message");
        log.info("Testing moderation for message: {}", message);

        ModerationResult result = moderationService.analyzeContent(message);
        return ResponseEntity.ok(result);
    }
}
