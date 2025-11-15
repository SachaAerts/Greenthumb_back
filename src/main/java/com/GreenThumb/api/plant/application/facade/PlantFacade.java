package com.GreenThumb.api.plant.application.facade;

import com.GreenThumb.api.plant.application.dto.PlantDto;

import java.util.List;

public interface PlantFacade {
    List<PlantDto> getAllPlants();

    List<PlantDto> getAllPlantsByUsername(String username);
}
