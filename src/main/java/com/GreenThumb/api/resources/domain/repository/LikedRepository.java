package com.GreenThumb.api.resources.domain.repository;

public interface LikedRepository {
    boolean existsById(Long userId, Long resourceId);

    void deleteById(Long userId, Long resourceId);

    void save(Long userId, Long resourceId);
}
