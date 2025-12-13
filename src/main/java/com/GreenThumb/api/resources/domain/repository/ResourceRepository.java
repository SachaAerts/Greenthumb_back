package com.GreenThumb.api.resources.domain.repository;

import com.GreenThumb.api.resources.domain.entity.Resource;
import com.GreenThumb.api.user.domain.exception.NoFoundException;

import java.util.List;
import java.util.Optional;

public interface ResourceRepository {
    List<Resource> getThreeResource();
    List<Resource> getAllResource();
    Optional<Resource> getResourceBySlug(String slug);

    Resource getResource(String slug);

    boolean existsBySlug(String slug);

    Long findIdBySlug(String slug) throws NoFoundException;

    void incrementLikeCount(Long resourceId);

    void decrementLikeCount(Long resourceId);

    int getLikeById(Long resourceId);

    void save(Resource resource);

    void deleteBySlug(String slug);
}
