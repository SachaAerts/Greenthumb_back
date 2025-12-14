package com.GreenThumb.api.plant.domain.repository;

import com.GreenThumb.api.plant.application.dto.PlantApiDto;

public interface PlantApiRepository {
    void createPlantApi(PlantApiDto newPlantApi);
}
