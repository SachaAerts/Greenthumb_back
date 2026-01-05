package com.GreenThumb.api.forum.infrastructure.repository;

import com.GreenThumb.api.forum.infrastructure.entity.ChannelEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SpringDataChannelRepo extends JpaRepository<ChannelEntity, Long> {

    @Query("""
        SELECT DISTINCT c FROM ChannelEntity c
        LEFT JOIN FETCH c.threads t
        ORDER BY t.isPinned DESC, t.createdAt DESC
    """)
    List<ChannelEntity> findAllWithThreadOrderbyPinned();

    Optional<ChannelEntity> findByName(String name);

    boolean existsByName(String name);
}