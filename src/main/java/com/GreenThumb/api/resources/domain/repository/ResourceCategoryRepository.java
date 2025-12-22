package com.GreenThumb.api.resources.domain.repository;

import com.GreenThumb.api.resources.domain.entity.Category;
import com.GreenThumb.api.resources.infrastructure.entity.ResourceCategoryEntity;

import java.util.List;

public interface ResourceCategoryRepository {

    List<Category> getAllCategory();

    ResourceCategoryEntity toEntityAndCreateNewCategory(Category category);

    ResourceCategoryEntity toEntityAndCreateNewCategory(String name);
}
