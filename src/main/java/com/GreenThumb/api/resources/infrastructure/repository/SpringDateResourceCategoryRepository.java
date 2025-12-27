package com.GreenThumb.api.resources.infrastructure.repository;

import com.GreenThumb.api.resources.infrastructure.entity.ResourceCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringDateResourceCategoryRepository extends JpaRepository<ResourceCategoryEntity, Long> {
    Optional<ResourceCategoryEntity> findByLabel(String label);
}
