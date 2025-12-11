package com.GreenThumb.api.apigateway.service;

import com.GreenThumb.api.apigateway.mapper.ResourceMapper;
import com.GreenThumb.api.apigateway.dto.Resource;
import com.GreenThumb.api.resources.application.dto.LikedDto;
import com.GreenThumb.api.resources.application.dto.ResourceDto;
import com.GreenThumb.api.resources.application.service.LikedService;
import com.GreenThumb.api.resources.application.service.ResourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ResourceServiceApi {

    private final ResourceService resourceService;

    private final LikedService likedService;

    private final TokenService tokenService;

    public ResourceServiceApi(ResourceService resourceService, LikedService likedService, TokenService tokenService) {
        this.resourceService = resourceService;
        this.likedService = likedService;
        this.tokenService = tokenService;
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

    public LikedDto addLike(String token, String resourceSlug) {
        String username = tokenService.extractUsername(token);
        log.debug("[Username] => " + username);
        return likedService.addLike(resourceSlug, username);
    }
}
