package com.GreenThumb.api.resources.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

public record ResourceRequest(
        @NotBlank(message = "Le titre est obligatoire")
        @Size(max = 255, message = "Le titre ne peut pas dépasser 255 caractères")
        String title,

        @NotBlank(message = "Le résumé est obligatoire")
        @Size(max = 255, message = "Le résumé ne peut pas dépasser 255 caractères")
        String summary,

        @NotBlank(message = "Le contenu est obligatoire")
        String content,

        String picture,

        @Valid
        List<String> categories
) {
}
