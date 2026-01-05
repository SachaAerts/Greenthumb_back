package com.GreenThumb.api.plant.domain.repository;

import com.GreenThumb.api.plant.application.dto.PlantRegister;
import com.GreenThumb.api.plant.domain.entity.Plant;
import com.GreenThumb.api.plant.infrastructure.entity.PlantEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface PlantRepository {
    List<Plant> findAll();

    Page<Plant> findAllByUser_username(String username, Pageable pageable);

    Page<Plant> findAllByUser_usernameAndSearch(String username, String search, Pageable pageable);

    Long findIdBySlug(String slug);

    Optional<PlantEntity> findbyId(Long id);

    void createPlant(PlantRegister plant);

    String updateBySlug(String slug, PlantRegister plant);

    void deleteBySlug(String slug);

}
