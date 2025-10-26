package com.Greanthumb.api.resources.infrastructure.mapper;

import com.Greanthumb.api.resources.domain.entity.Resource;
import com.Greanthumb.api.resources.infrastructure.entity.ResourceEntity;

public class ResourceMapper {

    public static Resource toDomain(ResourceEntity resourceEntity) {
        return new Resource(resourceEntity.getTitle(), resourceEntity.getLight(), resourceEntity.getPictureUrl(),
                resourceEntity.getDescription(), resourceEntity.getCreationDate());
    }
}
