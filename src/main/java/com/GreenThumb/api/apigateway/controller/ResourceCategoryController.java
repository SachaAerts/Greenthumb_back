package com.GreenThumb.api.apigateway.controller;

import com.GreenThumb.api.apigateway.service.ResourceServiceApi;
import com.GreenThumb.api.resources.application.dto.CategoryDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/resourceCategories")
public class ResourceCategoryController {

    public final ResourceServiceApi resourceService;

    public ResourceCategoryController(ResourceServiceApi resourceService) {
        this.resourceService = resourceService;
    }

    @RequestMapping("")
    public ResponseEntity<List<CategoryDto>> getAllCategories() {
        return ResponseEntity.ok(resourceService.getAllCategory());
    }
}
