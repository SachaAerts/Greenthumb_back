package com.GreenThumb.api.plant.infrastructure.repository;

import com.GreenThumb.api.plant.infrastructure.entity.PlantEntity;
import feign.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface SpringDataPlantRepository extends JpaRepository<PlantEntity, Long> {
    List<PlantEntity> findAll();

    Page<PlantEntity> findAllByUser_username(String username, Pageable pageable);

    @Query("SELECT p FROM PlantEntity p WHERE p.user.username = :username AND (LOWER(p.commonName) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(p.scientificName) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<PlantEntity> findAllByUser_usernameAndSearch(@Param("username") String username, @Param("search") String search, Pageable pageable);

    @Query("""
        SELECT p FROM PlantEntity p\s
        WHERE p.user.username = :username
        AND (:search IS NULL OR :search = '' OR LOWER(p.commonName) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(p.scientificName) LIKE LOWER(CONCAT('%', :search, '%')))
        AND (:lifeCycle IS NULL OR p.duration IN :lifeCycle)
        AND (:waterNeed IS NULL OR p.waterNeed IN :waterNeed)
        AND (:lightLevel IS NULL OR p.lightLevel IN :lightLevel)
        AND (:soilType IS NULL OR p.soilType IN :soilType)
        AND (:petToxic IS NULL OR p.petToxic = :petToxic)
        AND (:humanToxic IS NULL OR p.humanToxic = :humanToxic)
        AND (:indoorFriendly IS NULL OR p.indoorFriendly = :indoorFriendly)
   \s""")
    Page<PlantEntity> findAllByUser_usernameWithFilters(
            @Param("username") String username,
            @Param("search") String search,
            @Param("lifeCycle") List<String> lifeCycle,
            @Param("waterNeed") List<String> waterNeed,
            @Param("lightLevel") List<String> lightLevel,
            @Param("soilType") List<String> soilType,
            @Param("petToxic") Boolean petToxic,
            @Param("humanToxic") Boolean humanToxic,
            @Param("indoorFriendly") Boolean indoorFriendly,
            Pageable pageable
    );

    @Query("SELECT p.id FROM PlantEntity p WHERE p.slug = :slug")
    Long findIdBySlug(@Param("slug") String slug);

}
