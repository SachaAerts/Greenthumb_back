package com.GreenThumb.api.apigateway.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateReportRequest (
        @NotNull(message = "L'id du message est obligatoire")
        Long messageId,

        @Size(max = 500, message = "Votre raison ne doit pas dépasser les 500 caractére")
        String reason
){
}
