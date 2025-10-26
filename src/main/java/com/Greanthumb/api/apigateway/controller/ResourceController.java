package com.Greanthumb.api.apigateway.controller;

import com.Greanthumb.api.apigateway.service.ResourceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/resources")
public class ResourceController {

    private final ResourceService resourceService;

    public ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @GetMapping("/threeResource")
    public ResponseEntity<?> getThreeResource() {
        return ResponseEntity.ok(resourceService.getThreeResource());
    }
}
