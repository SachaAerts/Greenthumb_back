package com.GreenThumb.api.resources.infrastructure.mapper;

import com.GreenThumb.api.resources.domain.entity.Category;
import com.GreenThumb.api.resources.domain.entity.Resource;
import com.GreenThumb.api.resources.infrastructure.entity.ResourceCategoryEntity;
import com.GreenThumb.api.resources.infrastructure.entity.ResourceEntity;

import java.util.stream.Collectors;

public class ResourceMapper {

    public static Resource toDomain(ResourceEntity resourceEntity) {
        return new Resource(
                resourceEntity.getSlug(),
                resourceEntity.getTitle(),
                resourceEntity.getSummary(),
                resourceEntity.getLike(),
                resourceEntity.getPictureUrl(),
                resourceEntity.getDescription(),
                resourceEntity.getCreationDate(),
                resourceEntity.getUser().getUsername(),
                resourceEntity.getCategories().stream()
                        .map(CategoryMapper::toDomain)
                        .collect(Collectors.toSet())
                );
    }
}
