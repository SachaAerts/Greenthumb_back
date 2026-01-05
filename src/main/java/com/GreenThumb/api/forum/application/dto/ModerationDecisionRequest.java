package com.GreenThumb.api.forum.application.dto;

import jakarta.validation.constraints.Size;

public record ModerationDecisionRequest(
        @Size(max = 500, message = "Le commentaire ne doit pas dépasser 500 caractères")
        String comment
) {
}
