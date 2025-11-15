package com.GreenThumb.api.apigateway.controller;

import com.GreenThumb.api.apigateway.service.PlantService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/plants")
public class PlantController {

    private final PlantService plantService;

    public PlantController(PlantService plantService) {
        this.plantService = plantService;
    }
}
