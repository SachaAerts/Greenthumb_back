package com.GreenThumb.api.apigateway.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record VerifyEmailRequest(
        @NotBlank(message = "L'adresse email est obligatoire")
        @Email(message = "L'adresse email doit être valide")
        String email,

        @NotBlank(message = "Le code de vérification est obligatoire")
        @Pattern(regexp = "^\\d{6}$", message = "Le code doit contenir exactement 6 chiffres")
        String code
) {
}