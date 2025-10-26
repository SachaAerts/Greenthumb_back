package com.Greanthumb.api.apigateway.service;

import com.Greanthumb.api.apigateway.Mapper.ResourceMapper;
import com.Greanthumb.api.apigateway.dto.Resource;
import com.Greanthumb.api.resources.infrastructure.application.ResourceService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResourceServiceApi {

    private final ResourceService resourceService;

    public ResourceServiceApi(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    public List<Resource> getThreeResource() {
        return resourceService.get3Resources().stream()
                .map(ResourceMapper::toDto)
                .toList();
    }
}
