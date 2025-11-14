package com.GreenThumb.api.plant.domain.objecValue;

import java.util.Map;

public record TemperatureStat(
        int temperatureMin,
        int temperatureMax
) {
    public Map<String, Integer> getMaxMinTemperature() {
        return Map.of("min", temperatureMin, "max", temperatureMax);
    }
}
