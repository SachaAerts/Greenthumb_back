package com.Greanthumb.api.apigateway.controller;

import com.Greanthumb.api.apigateway.service.ResourceServiceApi;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/resources")
public class ResourceController {

    private final ResourceServiceApi resourceService;

    public ResourceController(ResourceServiceApi resourceService) {
        this.resourceService = resourceService;
    }

    @GetMapping("/threeResources")
    public ResponseEntity<?> getThreeResource() {
        return ResponseEntity.ok(resourceService.getThreeResource());
    }
}
