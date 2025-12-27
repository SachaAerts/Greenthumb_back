package com.GreenThumb.api.resources.application.service;

import com.GreenThumb.api.resources.application.dto.CategoryDto;
import com.GreenThumb.api.resources.domain.repository.ResourceCategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final ResourceCategoryRepository resourceCategoryRepository;

    public CategoryService(ResourceCategoryRepository resourceCategoryRepository) {
        this.resourceCategoryRepository = resourceCategoryRepository;
    }

    public List<CategoryDto> getAllCategory() {
        return resourceCategoryRepository.getAllCategory().stream()
                .map(CategoryDto::toDto)
                .toList();
    }
}
