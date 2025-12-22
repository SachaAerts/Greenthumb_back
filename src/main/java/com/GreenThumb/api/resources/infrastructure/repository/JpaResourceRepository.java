package com.GreenThumb.api.resources.infrastructure.repository;

import com.GreenThumb.api.resources.application.dto.ResourceRequest;
import com.GreenThumb.api.resources.domain.entity.Resource;
import com.GreenThumb.api.resources.domain.repository.ResourceCategoryRepository;
import com.GreenThumb.api.resources.domain.repository.ResourceRepository;
import com.GreenThumb.api.resources.domain.service.ResourceStorageService;
import com.GreenThumb.api.resources.domain.utils.SlugGenerator;
import com.GreenThumb.api.resources.infrastructure.entity.ResourceCategoryEntity;
import com.GreenThumb.api.resources.infrastructure.entity.ResourceEntity;
import com.GreenThumb.api.resources.infrastructure.mapper.ResourceMapper;
import com.GreenThumb.api.user.application.service.UserService;
import com.GreenThumb.api.user.domain.exception.NoFoundException;
import com.GreenThumb.api.user.infrastructure.entity.UserEntity;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class JpaResourceRepository implements ResourceRepository {

    private final SpringDataResourceRepository resourceRepository;

    private final ResourceCategoryRepository categoryRepository;
    private final UserService userService;

    public JpaResourceRepository(
            SpringDataResourceRepository resourceRepository,
            UserService userService,
            ResourceCategoryRepository resourceCategoryRepository
    ) {
        this.resourceRepository = resourceRepository;
        this.userService = userService;
        this.categoryRepository = resourceCategoryRepository;
    }

    @Override
    public List<Resource> getThreeResource() {
        return resourceRepository.findTop3ByOrderByCreationDateDesc().stream()
                .map(ResourceMapper::toDomain)
                .toList();
    }

    @Override
    public List<Resource> getAllResource() {
        return resourceRepository.findAll().stream()
                .map(ResourceMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Resource> getResourceBySlug(String slug) {
        return resourceRepository.findBySlug(slug)
                .map(ResourceMapper::toDomain);
    }

    @Override
    public Resource getResource(String slug) {
        return resourceRepository.findBySlug(slug)
                .map(ResourceMapper::toDomain)
                .orElseThrow(() -> new NoFoundException("Article non trouvé"));
    }

    @Override
    public boolean existsBySlug(String slug) {
        return resourceRepository.existsBySlug(slug);
    }

    @Override
    public Long findIdBySlug(String slug) throws NoFoundException {
        return resourceRepository.findIdBySlug(slug)
                .orElseThrow(() -> new NoFoundException("L'articles n'existe pas"));
    }

    @Override
    public void incrementLikeCount(Long resourceId) {
        resourceRepository.incrementLikeCount(resourceId);
    }

    @Override
    public void decrementLikeCount(Long resourceId) {
        resourceRepository.decrementLikeCount(resourceId);
    }

    @Override
    public int getLikeById(Long resourceId) {
        return resourceRepository.getLikeById(resourceId);
    }

    @Override
    public void save(Resource resource) {
        Long userId = userService.getIdByUsername(resource.username());
        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);

        Set<ResourceCategoryEntity> categoryEntities = resource.categories().stream()
                .map(categoryRepository::toEntityAndCreateNewCategory)
                .collect(Collectors.toSet());

        ResourceEntity entity = ResourceEntity.builder()
                .slug(resource.slug())
                .title(resource.title())
                .summary(resource.summary())
                .like(resource.like())
                .pictureUrl(resource.urlPicture())
                .description(resource.text())
                .creationDate(resource.creationDate())
                .user(userEntity)
                .categories(categoryEntities)
                .build();

        resourceRepository.save(entity);
    }

    @Override
    @Transactional
    public void deleteBySlug(String slug) {
        resourceRepository.deleteBySlug(slug);
    }

    @Override
    @Transactional
    public void editResource(String oldSlug, ResourceRequest request) {
        ResourceEntity resource = resourceRepository.findBySlug(oldSlug)
                .orElseThrow(() -> new NoFoundException("La ressource n'existe pas"));

        editEntityResource(request, resource);

        resourceRepository.save(resource);
    }

    private void editEntityResource(ResourceRequest request, ResourceEntity resource) {
        if (request.title() != null) {
            resource.setTitle(request.title());
            String newSlug = generateSlug(request.title());
            resource.setSlug(newSlug);
        }

        if (request.summary() != null) {
            resource.setSummary(request.summary());
        }

        if (request.content() != null) {
            resource.setDescription(request.content());
        }

        if (request.categories() != null && !request.categories().isEmpty()) {
            resource.getCategories().clear();

            Set<ResourceCategoryEntity> newCategories = request.categories().stream()
                    .map(categoryRepository::toEntityAndCreateNewCategory)
                    .collect(Collectors.toSet());

            resource.getCategories().addAll(newCategories);
        }

        ResourceStorageService storageService = new ResourceStorageService();
        String newImage = storageService.replaceUserImage(resource.getPictureUrl(), request.picture());
        if (newImage != null) {
            resource.setPictureUrl(newImage);
        }
    }

    private String generateSlug(String title) {
        String baseSlug = SlugGenerator.generateSlug(title);
        String slug = baseSlug;
        int counter = 1;

        while (resourceRepository.existsBySlug(slug)) {
            slug = SlugGenerator.generateUniqueSlug(baseSlug, counter);
            counter++;
        }

        return slug;
    }
}
