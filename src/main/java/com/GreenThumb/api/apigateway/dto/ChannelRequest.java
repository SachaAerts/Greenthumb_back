package com.GreenThumb.api.apigateway.dto;

import jakarta.validation.constraints.NotBlank;

public record ChannelRequest(
        @NotBlank(message = "Le nom du channel est obligatoire")
        String name,

        @NotBlank(message = "Veuillez mettre une déscription")
        String description
) {
}
