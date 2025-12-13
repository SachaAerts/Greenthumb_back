package com.GreenThumb.api.apigateway.controller;

import com.GreenThumb.api.apigateway.dto.Resource;
import com.GreenThumb.api.apigateway.service.ResourceServiceApi;
import com.GreenThumb.api.apigateway.service.TokenExtractor;
import com.GreenThumb.api.resources.application.dto.LikedDto;
import com.GreenThumb.api.resources.application.dto.ResourceDto;
import com.GreenThumb.api.resources.application.dto.ResourceRequest;
import com.GreenThumb.api.user.domain.exception.NoFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/resources")
public class ResourceController {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private final ResourceServiceApi resourceService;

    private final TokenExtractor tokenExtractor;

    public ResourceController(ResourceServiceApi resourceService, TokenExtractor tokenExtractor) {
        this.resourceService = resourceService;
        this.tokenExtractor = tokenExtractor;
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

    @PostMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addResource(
            @RequestBody ResourceRequest request,
            @RequestHeader(value = AUTHORIZATION_HEADER, required = false) String authorizationHeader
    ) {
        String token = tokenExtractor.extractToken(authorizationHeader);

        resourceService.addResource(token, request);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{slug}/like")
    public ResponseEntity<LikedDto> addLike(
            @PathVariable String slug,
            @RequestHeader(value = AUTHORIZATION_HEADER, required = false) String authorizationHeader
    ) {
        String token = tokenExtractor.extractToken(authorizationHeader);
        LikedDto like = resourceService.addLike(token, slug);

        return ResponseEntity.ok(like);
    }

    @DeleteMapping("/{slug}")
    public ResponseEntity<?> deleteResource(@PathVariable String slug) {
        resourceService.deleteResource(slug);

        return ResponseEntity.ok(slug);
    }
}
