package com.GreenThumb.api.resources.infrastructure.repository;

import com.GreenThumb.api.resources.infrastructure.entity.LikedEntity;
import com.GreenThumb.api.resources.infrastructure.entity.LikedId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataLikedRepository extends JpaRepository<LikedEntity, LikedId> {
    boolean existsById(LikedId id);

    void deleteById(LikedId id);
}
