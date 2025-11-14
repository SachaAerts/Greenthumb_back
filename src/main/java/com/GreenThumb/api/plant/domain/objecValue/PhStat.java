package com.GreenThumb.api.plant.domain.objecValue;

import java.util.Map;

public record PhStat(
        double soilPhMin,
        double soilPhMax
) {
    public Map<String, Double> getMaxMinPh() {
        return Map.of("min", soilPhMin, "max", soilPhMax);
    }
}
