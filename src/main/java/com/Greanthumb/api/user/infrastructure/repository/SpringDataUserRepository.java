package com.Greanthumb.api.user.infrastructure.repository;

import com.Greanthumb.api.user.infrastructure.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataUserRepository extends JpaRepository<UserEntity, Long> { }
