package com.GreenThumb.api.apigateway.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ResendVerificationEmailRequest(
        @NotBlank(message = "L'adresse email est obligatoire")
        @Email(message = "L'adresse email n'est pas valide")
        String email
) {
}