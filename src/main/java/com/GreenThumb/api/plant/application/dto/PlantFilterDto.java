package com.GreenThumb.api.plant.application.dto;

import java.util.List;

/**
 * DTO pour les filtres de recherche de plantes
 */
public record PlantFilterDto(
        List<String> lifeCycle,
        List<String> waterNeed,
        List<String> lightLevel,
        List<String> soilType,
        Boolean petToxic,
        Boolean humanToxic,
        Boolean indoorFriendly
) {
    /**
     * Vérifie si au moins un filtre est actif
     */
    public boolean hasActiveFilters() {
        return (lifeCycle != null && !lifeCycle.isEmpty()) ||
                (waterNeed != null && !waterNeed.isEmpty()) ||
                (lightLevel != null && !lightLevel.isEmpty()) ||
                (soilType != null && !soilType.isEmpty()) ||
                petToxic != null ||
                humanToxic != null ||
                indoorFriendly != null;
    }
}

