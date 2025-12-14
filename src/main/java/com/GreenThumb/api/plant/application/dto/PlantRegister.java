package com.GreenThumb.api.plant.application.dto;

import jakarta.validation.constraints.NotBlank;

public record PlantRegister(
        @NotBlank
        String slug,

        @NotBlank
        String scientificName,

        @NotBlank
        String commonName,

        @NotBlank
        String imageUrl,

        String lifeCycle,

        String waterNeed,

        String lightLevel,

        String soilType,

        Double soilPhMin,

        Double soilPhMax,

        Integer temperatureMin,

        Integer temperatureMax,

        Boolean humidityNeed,

        String bloomMonthStart,

        String bloomMonthEnd,

        Boolean petToxic,

        Boolean humanToxic,

        Boolean indoorFriendly,

        String description
) {}
