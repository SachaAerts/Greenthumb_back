package com.GreenThumb.api.apigateway.service;

import com.GreenThumb.api.plant.application.dto.PlantDto;
import com.GreenThumb.api.plant.application.facade.PlantFacade;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlantService {

    private final PlantFacade plantModule;

    public PlantService(PlantFacade plantModule) {
        this.plantModule = plantModule;
    }

    public List<PlantDto> getAllPlants() {
        return plantModule.getAllPlants();
    }
}
