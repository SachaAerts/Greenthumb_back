package com.GreenThumb.api.resources.application.service;

import com.GreenThumb.api.resources.application.dto.ResourceDto;
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

    public List<ResourceDto> getAllResource() {
        List<Resource> resources = resourceRepository.getAllResource();

        return resources.stream()
                .map((ResourceDto::to))
                .toList();
    }
}
