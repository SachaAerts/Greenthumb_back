package com.GreenThumb.api.plant.infrastructure.repository;

import com.GreenThumb.api.plant.infrastructure.entity.PlantEntity;
import feign.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface SpringDataPlantRepository extends JpaRepository<PlantEntity, Long> {
    List<PlantEntity> findAll();

    Page<PlantEntity> findAllByUser_username(String username, Pageable pageable);

    @Query("SELECT p.id FROM PlantEntity p WHERE p.slug = :slug")
    Long findIdBySlug(@Param("slug") String slug);

    Optional<PlantEntity> findBySlug(String slug);

}
