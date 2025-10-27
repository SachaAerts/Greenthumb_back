package com.GreenThumb.api.resources.infrastructure.application;

import com.GreenThumb.api.resources.domain.entity.Resource;
import com.GreenThumb.api.resources.domain.repository.ResourceRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResourceService {
    private final ResourceRepository resourceRepository;

    public ResourceService(ResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
    }

    public List<Resource> get3Resources() {
        return resourceRepository.getThreeResource();
    }
}
