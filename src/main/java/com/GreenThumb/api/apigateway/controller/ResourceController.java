package com.GreenThumb.api.apigateway.controller;

import com.GreenThumb.api.apigateway.dto.Resource;
import com.GreenThumb.api.apigateway.service.ResourceServiceApi;
import com.GreenThumb.api.resources.application.dto.ResourceDto;
import com.GreenThumb.api.user.domain.exception.NoFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/resources")
public class ResourceController {

    private final ResourceServiceApi resourceService;

    public ResourceController(ResourceServiceApi resourceService) {
        this.resourceService = resourceService;
    }

    @GetMapping("/three-resources")
    public ResponseEntity<List<Resource>> getThreeResource() {
        List<Resource> resources = resourceService.getThreeResource();

        return ResponseEntity.ok(resources);
    }

    @GetMapping("")
    public ResponseEntity<List<ResourceDto>> getAllResource() {
        return ResponseEntity.ok(resourceService.getAllResource());
    }

    @GetMapping("/{slug}")
    public ResponseEntity<ResourceDto> getResourceBySlug(@PathVariable String slug) {
        return resourceService.getResourceBySlug(slug)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/exist/{slug}")
    public ResponseEntity<?> getExistSlug(@PathVariable String slug) {
        if (resourceService.existBySlug(slug)) {
            return ResponseEntity.noContent().build();
        } else {
            throw new NoFoundException("Slug incorrect");
        }
    }
}
