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

    @NotNull(message = "Le type de destinataires doit être spécifié")
    RecipientType recipientType,

    List<String> recipientUsernames
) {
    public enum RecipientType {
        ALL_USERS,
        STAFF_ONLY,
        SPECIFIC_USERS
    }

    public BulkEmailRequest {
        if (recipientType == RecipientType.SPECIFIC_USERS &&
            (recipientUsernames == null || recipientUsernames.isEmpty())) {
            throw new IllegalArgumentException(
                "La liste des destinataires est obligatoire pour le type SPECIFIC_USERS"
            );
        }
    }
}
