package com.GreenThumb.api.plant.domain.entity;

import com.GreenThumb.api.plant.domain.objecValue.PlantStat;

import java.util.List;

public record Plant(
    String slug,
    String scientificName,
    String commonName,
    String imageUrl,
    String description,
    PlantStat stat
) {
}
