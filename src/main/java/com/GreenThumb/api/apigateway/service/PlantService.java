package com.GreenThumb.api.apigateway.service;

import com.GreenThumb.api.infrastructure.service.RedisService;
import com.GreenThumb.api.plant.application.dto.PlantApiDto;
import com.GreenThumb.api.plant.application.dto.PlantRegister;
import com.GreenThumb.api.plant.domain.repository.IPlantApiService;
import com.GreenThumb.api.plant.domain.repository.PlantApiRepository;
import com.GreenThumb.api.plant.domain.repository.PlantRepository;
import com.GreenThumb.api.plant.infrastructure.entity.api.TreflePlantResponse;
import com.GreenThumb.api.plant.infrastructure.mapper.PlantMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PlantService {

    private final IPlantApiService plantApiService;
    private final PlantRepository plantRepository;
    private final PlantApiRepository plantApiRepo;
    private final RedisService redisService;
    private static final int RESULTS_PER_PAGE = 20;

    public PlantService(IPlantApiService plantApiService, PlantRepository plantRepository,
                        PlantApiRepository plantApiRepo, RedisService redisService) {
        this.plantApiService = plantApiService;
        this.plantRepository = plantRepository;
        this.plantApiRepo = plantApiRepo;
        this.redisService = redisService;
    }

    public Page<PlantApiDto> searchPlants(String query, int page) {
        log.info("Processing plant search request: query='{}', page={}", query, page);

        TreflePlantResponse trefleResponse = plantApiService.searchPlants(query, page);

        List<PlantApiDto> plants = trefleResponse.getData() != null
                ? trefleResponse.getData().stream()
                    .map(PlantMapper::mapToPlantDTO)
                    .collect(Collectors.toList())
                : Collections.emptyList();

        int totalResults = trefleResponse.getMeta() != null && trefleResponse.getMeta().getTotal() != null
                ? trefleResponse.getMeta().getTotal()
                : 0;

        int totalPages = (int) Math.ceil((double) totalResults / RESULTS_PER_PAGE);

        Pageable pageable = PageRequest.of(page - 1, RESULTS_PER_PAGE);

        log.info("Returning {} plants for query '{}' (page {}/{})", plants.size(), query, page, totalPages);
        return new PageImpl<>(plants, pageable, totalResults);
    }

    public void createPlant(PlantRegister plantRegister) {
        plantRepository.createPlant(plantRegister);
        redisService.delete(plantRegister.username());
    }

    public void createPlantApi(PlantApiDto plantApiDto) {
        plantApiRepo.createPlantApi(plantApiDto);
    }
}
