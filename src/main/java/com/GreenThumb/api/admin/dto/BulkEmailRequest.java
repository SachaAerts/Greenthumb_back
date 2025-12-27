package com.GreenThumb.api.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record BulkEmailRequest(
    @NotBlank(message = "Le sujet est obligatoire")
    @Size(max = 200, message = "Le sujet ne peut pas dépasser 200 caractères")
    String subject,

    @NotBlank(message = "Le contenu est obligatoire")
    @Size(max = 5000, message = "Le contenu ne peut pas dépasser 5000 caractères")
    String content,

    @NotNull(message = "Le paramètre 'sendToAll' est obligatoire")
    Boolean sendToAll,

    List<String> recipientUsernames
) {
    public BulkEmailRequest {
        if (sendToAll != null && !sendToAll &&
            (recipientUsernames == null || recipientUsernames.isEmpty())) {
            throw new IllegalArgumentException(
                "La liste des destinataires est obligatoire quand 'sendToAll' est false"
            );
        }
    }
}
