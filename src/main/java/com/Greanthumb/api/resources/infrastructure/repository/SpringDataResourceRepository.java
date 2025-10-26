package com.Greanthumb.api.resources.infrastructure.repository;

import com.Greanthumb.api.resources.infrastructure.entity.ResourceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpringDataResourceRepository extends JpaRepository<ResourceEntity, Long> {

    List<ResourceEntity> findTop3ByOrderByCreationDateDesc();
}
