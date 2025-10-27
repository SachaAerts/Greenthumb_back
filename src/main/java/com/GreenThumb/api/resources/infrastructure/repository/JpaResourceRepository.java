package com.GreenThumb.api.resources.infrastructure.repository;

import com.GreenThumb.api.resources.domain.entity.Resource;
import com.GreenThumb.api.resources.domain.repository.ResourceRepository;
import com.GreenThumb.api.resources.infrastructure.mapper.ResourceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
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
}
