package com.GreenThumb.api.resources.infrastructure.repository;

import com.GreenThumb.api.resources.infrastructure.entity.ResourceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SpringDataResourceRepository extends JpaRepository<ResourceEntity, Long> {

    List<ResourceEntity> findTop3ByOrderByCreationDateDesc();

    Optional<ResourceEntity> findBySlug(String slug);
}
