package com.GreenThumb.api.plant.application.facade;

import com.GreenThumb.api.plant.application.dto.PlantDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PlantFacade {
    List<PlantDto> getAllPlants();

    Page<PlantDto> getAllPlantsByUsername(String username, Pageable pageable);

    long countTask();
}
