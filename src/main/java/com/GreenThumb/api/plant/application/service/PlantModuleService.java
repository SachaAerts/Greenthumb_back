package com.GreenThumb.api.plant.application.service;

import com.GreenThumb.api.plant.application.dto.PlantDto;
import com.GreenThumb.api.plant.domain.repository.PlantRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlantModuleService {

    private final PlantRepository plantRepository;

    public PlantModuleService(PlantRepository plantRepository) {
        this.plantRepository = plantRepository;
    }
    public List<PlantDto> findAll(){
        return plantRepository.findAll().stream()
                .map(PlantDto::toDomain)
                .toList();
    }

    public List<PlantDto> findAllByUser_username(String username) {
        return plantRepository.findAllByUser_username(username).stream()
                .map(PlantDto::toDomain)
                .toList();
    }
}
