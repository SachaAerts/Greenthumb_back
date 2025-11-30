package com.GreenThumb.api.plant.application.dto;

import com.GreenThumb.api.plant.domain.entity.Plant;

import java.util.List;

public record PlantDto(
        String slug,
        String scientificName,
        String commonName,
        String imageUrl,
        List<TaskDto> tasks
) {
    public static PlantDto toDomain(Plant plant) {
        return new PlantDto(
                plant.slug(),
                plant.scientificName(),
                plant.commonName(),
                plant.imageUrl(),
                List.of()
        );
    }
}
