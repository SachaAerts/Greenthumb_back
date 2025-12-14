package com.GreenThumb.api.plant.domain.repository;

import com.GreenThumb.api.plant.application.dto.PlantRegister;
import com.GreenThumb.api.plant.domain.entity.Plant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PlantRepository {
    List<Plant> findAll();

    Page<Plant> findAllByUser_username(String username, Pageable pageable);

    Long findIdBySlug(String slug);

    void createPlant(PlantRegister plant);
}
