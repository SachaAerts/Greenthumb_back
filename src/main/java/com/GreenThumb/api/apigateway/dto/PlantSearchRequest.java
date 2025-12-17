package com.GreenThumb.api.apigateway.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PlantSearchRequest(
        @NotBlank(message = "Query parameter cannot be empty")
        @Size(min = 2, max = 100, message = "Query must be between 2 and 100 characters")
        String query,

        @Min(value = 1, message = "Page must be at least 1")
        @Max(value = 1000, message = "Page cannot exceed 1000")
        Integer page
) {
    public PlantSearchRequest {
        if (page == null) {
            page = 1;
        }
    }

    public String getTrimmedQuery() {
        return query != null ? query.trim() : null;
    }
}
