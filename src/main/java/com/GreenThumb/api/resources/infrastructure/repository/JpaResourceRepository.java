package com.GreenThumb.api.resources.infrastructure.repository;

import com.GreenThumb.api.resources.domain.entity.Resource;
import com.GreenThumb.api.resources.domain.repository.ResourceRepository;
import com.GreenThumb.api.resources.infrastructure.mapper.ResourceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JpaResourceRepository implements ResourceRepository {

    private final SpringDataResourceRepository resourceRepository;

    public JpaResourceRepository(SpringDataResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
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
}
