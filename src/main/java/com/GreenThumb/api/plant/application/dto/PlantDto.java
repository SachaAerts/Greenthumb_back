package com.GreenThumb.api.plant.application.dto;

import com.GreenThumb.api.plant.domain.entity.Plant;

public record PlantDto(
        String scientificName,
        String commonName,
        String imageUrl
) {
    public static PlantDto toDomain(Plant plant) {
        return new PlantDto(
                plant.scientificName(),
                plant.commonName(),
                plant.imageUrl()
        );
    }
}
