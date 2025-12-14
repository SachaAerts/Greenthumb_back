package com.GreenThumb.api.plant.infrastructure.repository;

import com.GreenThumb.api.plant.application.dto.PlantApiDto;
import com.GreenThumb.api.plant.application.dto.PlantRegister;
import com.GreenThumb.api.plant.domain.entity.Plant;
import com.GreenThumb.api.plant.domain.repository.PlantRepository;
import com.GreenThumb.api.plant.domain.services.PlantImageStorageService;
import com.GreenThumb.api.plant.infrastructure.entity.PlantApiEntity;
import com.GreenThumb.api.plant.infrastructure.entity.PlantEntity;
import com.GreenThumb.api.plant.infrastructure.mapper.PlantMapper;
import com.GreenThumb.api.user.infrastructure.entity.UserEntity;
import com.GreenThumb.api.user.infrastructure.repository.SpringDataUserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class JpaPlantRepository implements PlantRepository {

    private final SpringDataPlantRepository plantRepository;
    private final SpringDataUserRepository userRepository;
    private final PlantImageStorageService plantImageStorageService;

    public JpaPlantRepository(SpringDataPlantRepository plantRepository,
                              SpringDataUserRepository userRepository,
                              PlantImageStorageService plantImageStorageService) {
        this.plantRepository = plantRepository;
        this.userRepository = userRepository;
        this.plantImageStorageService = plantImageStorageService;
    }

    public List<Plant> findAll() {
        return plantRepository.findAll().stream()
                .map((PlantMapper::toDomain))
                .toList();
    }

    public Page<Plant> findAllByUser_username(String username, Pageable pageable) {
        return plantRepository.findAllByUser_username(username, pageable)
                .map(PlantMapper::toDomain);
    }

    @Override
    @Transactional
    public void createPlant(PlantRegister newPlant) {
        UserEntity user = getUserAuthenticated();
        String processedImageUrl = plantImageStorageService.processPlantImage(newPlant.imageUrl());
        PlantEntity plantEntity = PlantMapper.toEntity(newPlant, user, processedImageUrl);
        plantRepository.save(plantEntity);
    }

    private UserEntity getUserAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }

    public Long findIdBySlug(String slug) {
        return plantRepository.findIdBySlug(slug);
    }
}
