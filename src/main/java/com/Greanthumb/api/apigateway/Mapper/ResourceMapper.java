package com.Greanthumb.api.apigateway.Mapper;

import com.Greanthumb.api.apigateway.dto.Resource;

public class ResourceMapper {

    public static Resource toDto(com.Greanthumb.api.resources.domain.entity.Resource resource) {
        return new Resource(resource.title(), resource.creationDate().toString(), resource.urlPicture());
    }
}
