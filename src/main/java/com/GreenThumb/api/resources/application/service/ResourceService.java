package com.GreenThumb.api.resources.application.service;

import com.GreenThumb.api.resources.application.dto.ResourceDto;
import com.GreenThumb.api.resources.application.dto.ResourceRequest;
import com.GreenThumb.api.resources.domain.entity.Category;
import com.GreenThumb.api.resources.domain.entity.Resource;
import com.GreenThumb.api.resources.domain.repository.ResourceRepository;
import com.GreenThumb.api.resources.domain.service.ResourceStorageService;
import com.GreenThumb.api.resources.domain.utils.SlugGenerator;
import com.GreenThumb.api.resources.infrastructure.entity.ResourceEntity;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ResourceService {
    private final ResourceRepository resourceRepository;
    private final ResourceStorageService storageService;

    public ResourceService(ResourceRepository resourceRepository, ResourceStorageService storageService) {
        this.resourceRepository = resourceRepository;
        this.storageService = storageService;
    }

    public List<Resource> get3Resources() {
        return resourceRepository.getThreeResource();
    }

    public List<ResourceDto> getAllResource() {
        List<Resource> resources = resourceRepository.getAllResource();

        return resources.stream()
                .map((ResourceDto::to))
                .toList();
    }

    public Optional<ResourceDto> getResourceBySlug(String slug) {
        return resourceRepository.getResourceBySlug(slug)
                .map(ResourceDto::to);
    }

    public boolean existsBySlug(String slug) {
        return resourceRepository.existsBySlug(slug);
    }

    public void addResource(String username, ResourceRequest request) {
        Resource resource = toDomain(username, request);

        resourceRepository.save(resource);
    }

    public void deleteResource(String slug) {
        Resource resource = resourceRepository.getResource(slug);

        storageService.deleteImage(resource.urlPicture());

        resourceRepository.deleteBySlug(slug);
    }

    public void editResource(String slug, ResourceRequest request) {
        resourceRepository.editResource(slug, request);
    }

    private Resource toDomain(String username, ResourceRequest request) {
        String slug = generateSlug(request);
        String pathImage = storageService.storeResourceImage(request.picture());
        LocalDate date = LocalDate.now();

        return new Resource(
                slug,
                request.title(),
                request.summary(),
                0,
                pathImage,
                request.content(),
                date,
                username,
                request.categories().stream()
                        .map(Category::new)
                        .collect(Collectors.toSet())
        );
    }

    private String generateSlug(ResourceRequest resource) {
        String baseSlug = SlugGenerator.generateSlug(resource.title());
        String slug = baseSlug;
        int counter = 1;

        while (resourceRepository.existsBySlug(slug)) {
            slug = SlugGenerator.generateUniqueSlug(baseSlug, counter);
            counter++;
        }

        return slug;
    }
}
