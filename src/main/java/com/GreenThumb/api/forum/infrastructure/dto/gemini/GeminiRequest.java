package com.GreenThumb.api.forum.infrastructure.dto.gemini;

import java.util.List;

public record GeminiRequest(
        List<GeminiContent> contents,
        GeminiGenerationConfig generationConfig
) {
}
