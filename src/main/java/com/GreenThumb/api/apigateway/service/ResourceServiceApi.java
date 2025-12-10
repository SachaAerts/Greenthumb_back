package com.GreenThumb.api.apigateway.service;

import com.GreenThumb.api.apigateway.mapper.ResourceMapper;
import com.GreenThumb.api.apigateway.dto.Resource;
import com.GreenThumb.api.resources.application.dto.ResourceDto;
import com.GreenThumb.api.resources.application.service.ResourceService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    public List<ResourceDto> getAllResource() {
        List<ResourceDto> resources =  resourceService.getAllResource();

        return resources;
    }

    public Optional<ResourceDto> getResourceBySlug(String slug) {
        return resourceService.getResourceBySlug(slug);
    }

    public boolean existBySlug(String slug) {
        return resourceService.existsBySlug(slug);
    }
}
