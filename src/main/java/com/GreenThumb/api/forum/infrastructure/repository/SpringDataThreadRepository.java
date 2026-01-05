package com.GreenThumb.api.forum.infrastructure.repository;

import com.GreenThumb.api.forum.infrastructure.entity.ThreadEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataThreadRepository extends JpaRepository<ThreadEntity, Long> {
}
