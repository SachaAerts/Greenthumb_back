package com.GreenThumb.api.resources.application.dto;

import com.GreenThumb.api.resources.domain.entity.Category;

public record CategoryDto(
        String name
) {

    public static CategoryDto toDto(Category category) {
        return new CategoryDto(category.name());
    }
}
