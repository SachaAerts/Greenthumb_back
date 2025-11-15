package com.GreenThumb.api.plant.domain.repository;

import com.GreenThumb.api.plant.domain.entity.Plant;

import java.util.List;

public interface PlantRepository {
    List<Plant> findAll();

    List<Plant> findAllByUser_username(String username);
}
