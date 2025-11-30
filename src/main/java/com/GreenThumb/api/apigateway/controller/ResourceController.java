package com.GreenThumb.api.apigateway.controller;

import com.GreenThumb.api.apigateway.dto.Resource;
import com.GreenThumb.api.apigateway.service.ResourceServiceApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
        log.debug("Fetching three resources");

        List<Resource> resources = resourceService.getThreeResource();

        if (resources.isEmpty()) {
            log.debug("No resources found, returning empty list");
            return ResponseEntity.ok(resources);
        }

        log.debug("Found {} resources", resources.size());
        return ResponseEntity.ok(resources);
    }
}
