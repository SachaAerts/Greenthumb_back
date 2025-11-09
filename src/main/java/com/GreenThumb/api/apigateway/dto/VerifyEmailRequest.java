package com.GreenThumb.api.apigateway.dto;

import jakarta.validation.constraints.NotBlank;

public record VerifyEmailRequest(
        @NotBlank(message = "Le token de vérification est obligatoire")
        String token
) {
}