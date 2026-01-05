package com.GreenThumb.api.forum.infrastructure.dto.gemini;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GeminiGenerationConfig(
        @JsonProperty("response_mime_type") String responseMimeType,
        double temperature
) {
}
