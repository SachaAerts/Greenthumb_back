package com.GreenThumb.api.apigateway.dto;

import jakarta.validation.constraints.NotNull;

public record ThreadRequest(
        @NotNull
        String title,

        @NotNull
        String creator
) {
}
