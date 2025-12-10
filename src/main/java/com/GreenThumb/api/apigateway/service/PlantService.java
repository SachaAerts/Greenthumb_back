package com.GreenThumb.api.apigateway.service;

import com.GreenThumb.api.plant.application.dto.PaginationInfo;
import com.GreenThumb.api.plant.application.dto.PlantApiDto;
import com.GreenThumb.api.plant.application.dto.PlantSearchResponse;
import com.GreenThumb.api.plant.domain.repository.PlantApiRepository;
import com.GreenThumb.api.plant.infrastructure.entity.api.TreflePlantResponse;
import com.GreenThumb.api.plant.infrastructure.mapper.PlantMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PlantService {

    private final PlantApiRepository plantApiRepo;
    private static final int RESULTS_PER_PAGE = 20;

    public PlantService(PlantApiRepository plantApiRepo) {
        this.plantApiRepo = plantApiRepo;
    }

    public PlantSearchResponse searchPlants(String query, int page) {
        log.info("Processing plant search request: query='{}', page={}", query, page);

        TreflePlantResponse trefleResponse = plantApiRepo.searchPlants(query, page);

        List<PlantApiDto> plants = trefleResponse.getData() != null
                ? trefleResponse.getData().stream()
                    .map(PlantMapper::mapToPlantDTO)
                    .collect(Collectors.toList())
                : Collections.emptyList();

        int totalResults = trefleResponse.getMeta() != null && trefleResponse.getMeta().getTotal() != null
                ? trefleResponse.getMeta().getTotal()
                : 0;

        int totalPages = (int) Math.ceil((double) totalResults / RESULTS_PER_PAGE);

        PaginationInfo pagination = new PaginationInfo(
                page,
                totalResults,
                calculateNextPage(page, totalPages),
                calculatePreviousPage(page),
                totalPages,
                page < totalPages,
                page > 1
        );

        log.info("Returning {} plants for query '{}' (page {}/{})", plants.size(), query, page, totalPages);
        return new PlantSearchResponse(plants, pagination);
    }

    private Integer calculateNextPage(int currentPage, int totalPages) {
        return currentPage < totalPages ? currentPage + 1 : null;
    }

    private Integer calculatePreviousPage(int currentPage) {
        return currentPage > 1 ? currentPage - 1 : null;
    }
}
