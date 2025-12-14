package com.GreenThumb.api.apigateway.controller;

import com.GreenThumb.api.apigateway.dto.PlantSearchRequest;
import com.GreenThumb.api.apigateway.service.PlantService;
import com.GreenThumb.api.plant.application.dto.PlantApiDto;
import com.GreenThumb.api.plant.application.dto.PlantRegister;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/plants")
public class PlantController {
    private final PlantService plantService;

    public PlantController(PlantService plantService) {
        this.plantService = plantService;
    }

    @GetMapping("/search")
    public ResponseEntity<Page<PlantApiDto>> searchPlants(
            @Valid @ModelAttribute PlantSearchRequest request
    ) {

        log.info("Received search request: query='{}', page={}", request.getTrimmedQuery(), request.page());

        Page<PlantApiDto> response = plantService.searchPlants(request.getTrimmedQuery(), request.page());
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<?> createPlant(@Valid @RequestBody PlantRegister request) {
        plantService.createPlant(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/api")
    public ResponseEntity<?> createPlantApi(@Valid @RequestBody PlantApiDto request) {
        plantService.createPlantApi(request);
        return ResponseEntity.ok().build();
    }
}
