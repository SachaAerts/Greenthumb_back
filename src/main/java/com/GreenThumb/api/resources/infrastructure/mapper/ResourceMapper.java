package com.GreenThumb.api.resources.infrastructure.mapper;

import com.GreenThumb.api.resources.domain.entity.Resource;
import com.GreenThumb.api.resources.infrastructure.entity.ResourceEntity;

public class ResourceMapper {

    public static Resource toDomain(ResourceEntity resourceEntity) {
        return new Resource(
                resourceEntity.getTitle(),
                resourceEntity.getLike(),
                resourceEntity.getPictureUrl(),
                resourceEntity.getDescription(),
                resourceEntity.getCreationDate(),
                resourceEntity.getUser().getUsername()
                );
    }
}
