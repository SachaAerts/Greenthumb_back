package com.GreenThumb.api.apigateway.controller;

import com.GreenThumb.api.infrastructure.dto.gemini.ModerationResult;
import com.GreenThumb.api.infrastructure.service.GeminiModerationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/test-moderation")
public class PublicModerationController {

    private final GeminiModerationService moderationService;

    public PublicModerationController(GeminiModerationService moderationService) {
        this.moderationService = moderationService;
        log.info("PublicModerationController initialized - NO AUTH REQUIRED");
    }

    @GetMapping
    public ResponseEntity<String> hello() {
        log.info("GET /test-moderation called");
        return ResponseEntity.ok("Moderation service is ready! Use POST with {\"message\": \"your text\"}");
    }

    @PostMapping
    public ResponseEntity<ModerationResult> testModeration(@RequestBody Map<String, String> request) {
        log.info("POST /test-moderation called - NO AUTH");
        String message = request.get("message");
        log.info("Testing moderation for: {}", message);

        ModerationResult result = moderationService.analyzeContent(message);
        log.info("Moderation result: {}", result);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/quick")
    public ResponseEntity<ModerationResult> quickTest(@RequestParam(defaultValue = "Test message") String text) {
        log.info("GET /test-moderation/quick called with text: {}", text);
        ModerationResult result = moderationService.analyzeContent(text);
        return ResponseEntity.ok(result);
    }
}