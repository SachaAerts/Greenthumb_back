package com.GreenThumb.api.user.infrastructure.repository;

import com.GreenThumb.api.user.infrastructure.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataRoleRepository extends JpaRepository<RoleEntity, Long> {

}
