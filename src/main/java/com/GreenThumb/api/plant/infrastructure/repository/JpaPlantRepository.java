package com.GreenThumb.api.plant.infrastructure.repository;


import com.GreenThumb.api.plant.domain.entity.Plant;
import com.GreenThumb.api.plant.domain.repository.PlantRepository;
import com.GreenThumb.api.plant.infrastructure.mapper.PlantMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class JpaPlantRepository implements PlantRepository {

    private final SpringDataPlantRepository plantRepository;
    public JpaPlantRepository(SpringDataPlantRepository plantRepository) {
        this.plantRepository = plantRepository;
    }

    public List<Plant> findAll() {
        return plantRepository.findAll().stream()
                .map((PlantMapper::toDomain))
                .toList();
    }

    public List<Plant> findAllByUser_username(String username) {
        return plantRepository.findAllByUser_username(username).stream()
                .map(PlantMapper::toDomain)
                .toList();
    }

    public Long findIdBySlug(String slug) {
        return plantRepository.findIdBySlug(slug);
    }
}
