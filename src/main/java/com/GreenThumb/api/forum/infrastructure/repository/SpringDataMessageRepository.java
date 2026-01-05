package com.GreenThumb.api.forum.infrastructure.repository;

import com.GreenThumb.api.forum.infrastructure.entity.MessageEntity;
import com.GreenThumb.api.user.infrastructure.entity.UserEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SpringDataMessageRepository extends JpaRepository<MessageEntity, Long> {

    List<MessageEntity> findByThreadId(Long threadId);

    List<MessageEntity> findByUser(UserEntity user);

    @Query("SELECT m FROM MessageEntity m WHERE " +
            "(m.aiModerationChecked = false OR " +
            "(m.aiModerationChecked = true AND (m.aiModerationValid = true OR m.aiModerationValid IS NULL))) " +
            "ORDER BY m.createdAt DESC")
    List<MessageEntity> findTop3MessagesForModeration(Pageable pageable);
}
