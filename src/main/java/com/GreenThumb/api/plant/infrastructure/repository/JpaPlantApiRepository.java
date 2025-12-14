package com.GreenThumb.api.plant.infrastructure.repository;

import com.GreenThumb.api.plant.application.dto.PlantApiDto;
import com.GreenThumb.api.plant.domain.repository.PlantApiRepository;
import com.GreenThumb.api.plant.infrastructure.entity.PlantApiEntity;
import com.GreenThumb.api.plant.infrastructure.mapper.PlantMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class JpaPlantApiRepository implements PlantApiRepository {
    private final SpringDataPlantApiRepository plantApiRepository;

    public JpaPlantApiRepository(SpringDataPlantApiRepository plantApiRepository) {
        this.plantApiRepository = plantApiRepository;
    }

    @Override
    @Transactional
    public void createPlantApi(PlantApiDto newPlantApi) {
        PlantApiEntity newPlantApiEntity = PlantMapper.toPlantApiEntity(newPlantApi);

        if(!plantApiRepository.existsBySlug(newPlantApiEntity.getSlug())) {
            plantApiRepository.save(newPlantApiEntity);
        }
    }
}
