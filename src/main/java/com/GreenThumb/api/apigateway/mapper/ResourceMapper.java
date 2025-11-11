package com.GreenThumb.api.apigateway.mapper;

import com.GreenThumb.api.apigateway.dto.Resource;

public class ResourceMapper {

    public static Resource toDto(com.GreenThumb.api.resources.domain.entity.Resource resource) {
        return new Resource(resource.title(), resource.creationDate().toString(), resource.urlPicture());
    }
}
