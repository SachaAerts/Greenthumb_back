package com.GreenThumb.api.resources.infrastructure.repository;

import com.GreenThumb.api.resources.domain.entity.Category;
import com.GreenThumb.api.resources.domain.repository.ResourceCategoryRepository;
import com.GreenThumb.api.resources.infrastructure.entity.ResourceCategoryEntity;
import com.GreenThumb.api.resources.infrastructure.mapper.CategoryMapper;
import com.GreenThumb.api.user.domain.exception.NoFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Slf4j
public class JpaResourceCategoryRepository implements ResourceCategoryRepository {

    private final SpringDateResourceCategoryRepository resourceCategoriesRepository;

    public JpaResourceCategoryRepository(SpringDateResourceCategoryRepository resourceCategoriesRepository) {
        this.resourceCategoriesRepository = resourceCategoriesRepository;
    }

    @Override
    public List<Category> getAllCategory() {
        log.info("Récuperation des categories");

        return resourceCategoriesRepository.findAll().stream()
                .map(CategoryMapper::toDomain)
                .toList();
    }

    @Override
    public ResourceCategoryEntity toEntityAndCreateNewCategory(Category category) {
        return resourceCategoriesRepository.findByLabel(category.name())
                .orElseGet(() -> {
                    ResourceCategoryEntity newCategory = ResourceCategoryEntity.builder()
                            .label(category.name())
                            .build();

                    return resourceCategoriesRepository.save(newCategory);
                });
    }

    @Override
    public ResourceCategoryEntity toEntityAndCreateNewCategory(String name) {
        return resourceCategoriesRepository.findByLabel(name)
                .orElseGet(() -> {
                    ResourceCategoryEntity newCategory = ResourceCategoryEntity.builder()
                            .label(name)
                            .build();

                    return resourceCategoriesRepository.save(newCategory);
                });
    }
}
