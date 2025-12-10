package com.GreenThumb.api.plant.domain.repository;

import com.GreenThumb.api.plant.infrastructure.entity.api.TreflePlantResponse;

public interface PlantApiRepository {
    TreflePlantResponse searchPlants(String query, int page);
}
