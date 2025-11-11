package com.GreenThumb.api.apigateway.controller;

import com.GreenThumb.api.apigateway.dto.Resource;
import com.GreenThumb.api.apigateway.service.ResourceServiceApi;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/resources")
public class ResourceController {

    private final ResourceServiceApi resourceService;

    public ResourceController(ResourceServiceApi resourceService) {
        this.resourceService = resourceService;
    }

    @GetMapping("/three-resources")
    public ResponseEntity<?> getThreeResource() {
        List<Resource> resources = resourceService.getThreeResource();

        if (resources.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(resources);
    }
}
