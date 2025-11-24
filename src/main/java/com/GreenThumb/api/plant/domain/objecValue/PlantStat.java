package com.GreenThumb.api.plant.domain.objecValue;

import java.util.Map;

public record PlantStat (
    String duration,
    Integer lightLevel,
    PhStat phStat,
    TemperatureStat temperatureStat,
    Integer humidityNeed,
    Toxic toxic,
    Boolean indoorFriendly
) {
    public Map<String, Double> getMaxMinPh() {
         return phStat.getMaxMinPh();
    }

    public Map<String, Integer> getMaxMinTemperature() {
        return temperatureStat.getMaxMinTemperature();
    }
}
