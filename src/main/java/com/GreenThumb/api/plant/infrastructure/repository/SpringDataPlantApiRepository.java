package com.GreenThumb.api.plant.infrastructure.repository;

import com.GreenThumb.api.plant.infrastructure.entity.PlantApiEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataPlantApiRepository extends JpaRepository<PlantApiEntity, Long> {
    boolean existsBySlug(String slug);
}
