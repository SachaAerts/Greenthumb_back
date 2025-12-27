package com.GreenThumb.api.resources.infrastructure.mapper;

import com.GreenThumb.api.forum.infrastructure.entity.CategoryEntity;
import com.GreenThumb.api.resources.domain.entity.Category;
import com.GreenThumb.api.resources.infrastructure.entity.ResourceCategoryEntity;

public class CategoryMapper {

    public static Category toDomain(ResourceCategoryEntity category) {
        return new Category(
                category.getLabel()
        );
    }
}
