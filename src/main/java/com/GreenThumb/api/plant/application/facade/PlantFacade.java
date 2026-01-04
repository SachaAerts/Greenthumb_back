package com.GreenThumb.api.plant.application.facade;

import com.GreenThumb.api.plant.application.dto.PageResponse;
import com.GreenThumb.api.plant.application.dto.PlantDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PlantFacade {
    List<PlantDto> getAllPlants();

    PageResponse<PlantDto> getAllPlantsByUsername(String username, Pageable pageable);

    PageResponse<PlantDto> getAllPlantsByUsernameAndSearch(String username, String search, Pageable pageable);

    long countTask(Long userId);
}
