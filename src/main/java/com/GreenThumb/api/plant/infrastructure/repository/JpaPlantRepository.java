package com.GreenThumb.api.plant.infrastructure.repository;

import com.GreenThumb.api.plant.application.dto.PlantApiDto;
import com.GreenThumb.api.plant.application.dto.PlantFilterDto;
import com.GreenThumb.api.plant.application.dto.PlantRegister;
import com.GreenThumb.api.plant.application.events.PlantEventPublisher;
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
import java.util.Optional;

@Repository
public class JpaPlantRepository implements PlantRepository {

    private final SpringDataPlantRepository plantRepository;
    private final SpringDataUserRepository userRepository;
    private final PlantImageStorageService plantImageStorageService;

    private final PlantEventPublisher eventPublisher;

    public JpaPlantRepository(
            SpringDataPlantRepository plantRepository,
            SpringDataUserRepository userRepository,
            PlantImageStorageService plantImageStorageService,
            PlantEventPublisher eventPublisher
    ) {
        this.plantRepository = plantRepository;
        this.userRepository = userRepository;
        this.plantImageStorageService = plantImageStorageService;
        this.eventPublisher = eventPublisher;
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
    public Page<Plant> findAllByUser_usernameAndSearch(String username, String search, Pageable pageable) {
        return plantRepository.findAllByUser_usernameAndSearch(username, search, pageable)
                .map(PlantMapper::toDomain);
    }

    @Override
    public Page<Plant> findAllByUser_usernameWithFilters(String username, String search, PlantFilterDto filters, Pageable pageable) {
        return plantRepository.findAllByUser_usernameWithFilters(
                username,
                search,
                filters.lifeCycle(),
                filters.waterNeed(),
                filters.lightLevel(),
                filters.soilType(),
                filters.petToxic(),
                filters.humanToxic(),
                filters.indoorFriendly(),
                pageable
        ).map(PlantMapper::toDomain);
    }

    @Override
    @Transactional
    public void createPlant(PlantRegister newPlant) {
        UserEntity user = getUserAuthenticated();
        String processedImageUrl = plantImageStorageService.processPlantImage(newPlant.imageUrl());

        // Générer un slug unique
        String uniqueSlug = generateUniqueSlug(newPlant.slug());

        PlantEntity plantEntity = PlantMapper.toEntity(newPlant, user, processedImageUrl);
        plantEntity.setSlug(uniqueSlug);

        PlantEntity savedPlant = plantRepository.save(plantEntity);

        eventPublisher.publishPlantCreated(
                savedPlant.getId(),
                user.getId(),
                savedPlant.getSlug(),
                savedPlant.getScientificName(),
                savedPlant.getCommonName(),
                savedPlant.getWaterNeed(),
                savedPlant.getDuration(),
                savedPlant.getLightLevel(),
                savedPlant.getHumidityNeed(),
                savedPlant.getIndoorFriendly()
        );
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

    @Override
    public Optional<PlantEntity> findbyId(Long id) {
        return plantRepository.findById(id);
    }

    @Override
    @Transactional
    public void deleteBySlug(String slug) {
        UserEntity user = getUserAuthenticated();
        PlantEntity plant = plantRepository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Plant not found with slug: " + slug));

        if (!plant.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You can only delete your own plants");
        }

        plantImageStorageService.deletePlantImage(plant.getImageUrl());
        plantRepository.delete(plant);
    }

    @Override
    @Transactional
    public String updateBySlug(String slug, PlantRegister plantRegister) {
        UserEntity user = getUserAuthenticated();
        PlantEntity plant = plantRepository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Plant not found with slug: " + slug));

        if (!plant.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You can only update your own plants");
        }

        // Si l'image a changé, on supprime l'ancienne et on sauvegarde la nouvelle
        String processedImageUrl = plant.getImageUrl();
        if (plantRegister.imageUrl() != null && !plantRegister.imageUrl().equals(plant.getImageUrl())) {
            plantImageStorageService.deletePlantImage(plant.getImageUrl());
            processedImageUrl = plantImageStorageService.processPlantImage(plantRegister.imageUrl());
        }

        // Si le nom scientifique a changé, on met à jour le slug
        String newSlug = plant.getSlug();
        if (!plant.getScientificName().equals(plantRegister.scientificName())) {
            String baseSlug = plantRegister.scientificName()
                    .toLowerCase()
                    .replaceAll("\\s+", "-")
                    .replaceAll("[^a-z0-9-]", "");
            newSlug = generateUniqueSlug(baseSlug);
            plant.setSlug(newSlug);
        }

        // Mise à jour des champs
        plant.setScientificName(plantRegister.scientificName());
        plant.setCommonName(plantRegister.commonName());
        plant.setImageUrl(processedImageUrl);
        plant.setDuration(plantRegister.lifeCycle());
        plant.setWaterNeed(plantRegister.waterNeed());
        plant.setLightLevel(plantRegister.lightLevel());
        plant.setSoilType(plantRegister.soilType());
        plant.setSoilPhMin(plantRegister.soilPhMin());
        plant.setSoilPhMax(plantRegister.soilPhMax());
        plant.setTemperatureMin(plantRegister.temperatureMin());
        plant.setTemperatureMax(plantRegister.temperatureMax());
        plant.setHumidityNeed(plantRegister.humidityNeed());
        plant.setBloomMonths(plantRegister.bloomMonthStart() + "-" + plantRegister.bloomMonthEnd());
        plant.setPetToxic(plantRegister.petToxic());
        plant.setHumanToxic(plantRegister.humanToxic());
        plant.setIndoorFriendly(plantRegister.indoorFriendly());
        plant.setDescription(plantRegister.description());

        plantRepository.save(plant);

        return newSlug;
    }

    /**
     * Génère un slug unique en ajoutant un suffixe numérique si le slug existe déjà.
     * Par exemple: "rosa-damascena" -> "rosa-damascena-2" si "rosa-damascena" existe déjà.
     */
    private String generateUniqueSlug(String baseSlug) {
        if (!plantRepository.existsBySlug(baseSlug)) {
            return baseSlug;
        }

        int suffix = 2;
        String candidateSlug;
        do {
            candidateSlug = baseSlug + "-" + suffix;
            suffix++;
        } while (plantRepository.existsBySlug(candidateSlug));

        return candidateSlug;
    }
}
