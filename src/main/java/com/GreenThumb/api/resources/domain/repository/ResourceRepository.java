package com.GreenThumb.api.resources.domain.repository;

import com.GreenThumb.api.resources.domain.entity.Resource;

import java.util.List;
import java.util.Optional;

public interface ResourceRepository {
    List<Resource> getThreeResource();
    List<Resource> getAllResource();
    Optional<Resource> getResourceBySlug(String slug);
}
