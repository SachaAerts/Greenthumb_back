package com.GreenThumb.api.plant.domain.entity;

import com.GreenThumb.api.plant.domain.objecValue.PlantStat;

public record Plant(
    String slug,
    String scientificName,
    String commonName,
    String imageUrl,
    String description,
    String lifeCycle,
    String waterNeed,
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
    PlantStat stat
) {
}
