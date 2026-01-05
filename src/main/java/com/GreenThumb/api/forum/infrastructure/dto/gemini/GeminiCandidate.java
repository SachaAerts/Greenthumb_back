package com.GreenThumb.api.forum.infrastructure.dto.gemini;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GeminiCandidate {
    private GeminiContent content;
}
