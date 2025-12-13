package com.GreenThumb.api.resources.infrastructure.repository;

import com.GreenThumb.api.resources.domain.entity.Resource;
import com.GreenThumb.api.resources.domain.repository.ResourceRepository;
import com.GreenThumb.api.resources.infrastructure.entity.ResourceEntity;
import com.GreenThumb.api.resources.infrastructure.mapper.ResourceMapper;
import com.GreenThumb.api.user.application.service.UserService;
import com.GreenThumb.api.user.domain.exception.NoFoundException;
import com.GreenThumb.api.user.infrastructure.entity.UserEntity;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public class JpaResourceRepository implements ResourceRepository {

    private final SpringDataResourceRepository resourceRepository;
    private final UserService userService;

    public JpaResourceRepository(SpringDataResourceRepository resourceRepository, UserService userService) {
        this.resourceRepository = resourceRepository;
        this.userService = userService;
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

        ResourceEntity entity = ResourceEntity.builder()
                .slug(resource.slug())
                .title(resource.title())
                .summary(resource.summary())
                .like(resource.like())
                .pictureUrl(resource.urlPicture())
                .description(resource.text())
                .creationDate(resource.creationDate())
                .user(userEntity)
                .build();

        resourceRepository.save(entity);
    }

    @Override
    @Transactional
    public void deleteBySlug(String slug) {
        resourceRepository.deleteBySlug(slug);
    }
}
