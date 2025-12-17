package com.GreenThumb.api.plant.application.dto;

import com.GreenThumb.api.plant.domain.entity.Plant;

import java.util.List;

public record PlantDto(
        String slug,
        String scientificName,
        String commonName,
        String imageUrl,
        String description,
        String lifeCycle,
        String lightLevel,
        String soilType,
        Double soilPhMin,
        Double soilPhMax,
        int temperatureMin,
        int temperatureMax,
        Boolean humidityNeed,
        String bloomMonth,
        Boolean petToxic,
        Boolean humanToxic,
        Boolean indoorFriendly,
        List<TaskDto> tasks
) {
    public static PlantDto toDomain(Plant plant) {
        return new PlantDto(
                plant.slug(),
                plant.scientificName(),
                plant.commonName(),
                plant.imageUrl(),
                plant.description(),
                plant.lifeCycle(),
                plant.lightLevel(),
                plant.soilType(),
                plant.soilPhMin(),
                plant.soilPhMax(),
                plant.temperatureMin(),
                plant.temperatureMax(),
                plant.humidityNeed(),
                plant.bloomMonth(),
                plant.petToxic(),
                plant.humanToxic(),
                plant.indoorFriendly(),
                List.of()
        );
    }
}
