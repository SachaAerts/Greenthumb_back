package com.GreenThumb.api.plant.domain.repository;

import com.GreenThumb.api.plant.infrastructure.entity.api.TreflePlantResponse;

public interface IPlantApiService {
    TreflePlantResponse searchPlants(String query, int page);
}
