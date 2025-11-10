package com.GreenThumb.api.user.domain.repository;

import com.GreenThumb.api.user.infrastructure.entity.RoleEntity;

public interface RoleRepository {
    RoleEntity getRoleEntity(String label);
}
