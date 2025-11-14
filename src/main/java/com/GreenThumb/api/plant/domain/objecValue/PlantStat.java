package com.GreenThumb.api.plant.domain.objecValue;

import java.util.Map;

public record PlantStat (
    String duration,
    int lightLevel,
    PhStat phStat,
    TemperatureStat temperatureStat,
    int humidityNeed,
    Toxic toxic,
    boolean indoorFriendly
) {
    public Map<String, Double> getMaxMinPh() {
         return phStat.getMaxMinPh();
    }

    public Map<String, Integer> getMaxMinTemperature() {
        return temperatureStat.getMaxMinTemperature();
    }
}
