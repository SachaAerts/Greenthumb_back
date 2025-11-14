package com.GreenThumb.api.plant.infrastructure.repository;

import com.GreenThumb.api.plant.infrastructure.entity.PlantEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpringDataPlantRepository extends JpaRepository<PlantEntity, Long> {

    List<PlantEntity> findAll();
}
