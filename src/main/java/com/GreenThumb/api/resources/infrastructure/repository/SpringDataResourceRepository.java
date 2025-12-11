package com.GreenThumb.api.resources.infrastructure.repository;

import com.GreenThumb.api.resources.infrastructure.entity.ResourceEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SpringDataResourceRepository extends JpaRepository<ResourceEntity, Long> {

    List<ResourceEntity> findTop3ByOrderByCreationDateDesc();

    Optional<ResourceEntity> findBySlug(String slug);

    boolean existsBySlug(String slug);

    @Query("SELECT r.id FROM ResourceEntity r WHERE r.slug = :slug")
    Optional<Long> findIdBySlug(@Param("slug") String slug);

    @Modifying
    @Query("UPDATE ResourceEntity r SET r.like = r.like + 1 WHERE r.id = :id")
    void incrementLikeCount(@Param("id") Long id);

    @Modifying
    @Query("UPDATE ResourceEntity r SET r.like = r.like - 1 WHERE r.id = :id")
    void decrementLikeCount(@Param("id") Long id);

    @Query("SELECT r.like FROM ResourceEntity r WHERE r.id = :id")
    int getLikeById(@Param("id") Long id);

}
