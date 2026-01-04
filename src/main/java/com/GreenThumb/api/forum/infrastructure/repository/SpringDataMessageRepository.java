package com.GreenThumb.api.forum.infrastructure.repository;

import com.GreenThumb.api.forum.infrastructure.entity.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SpringDataMessageRepository extends JpaRepository<MessageEntity, Long> {

    @Query("SELECT m FROM MessageEntity m WHERE m.thread.id = :threadId " +
           "AND (m.aiModerationValid IS NULL OR m.aiModerationValid = true) " +
           "ORDER BY m.createdAt ASC")
    List<MessageEntity> findByThreadId(@Param("threadId") Long threadId);
}
