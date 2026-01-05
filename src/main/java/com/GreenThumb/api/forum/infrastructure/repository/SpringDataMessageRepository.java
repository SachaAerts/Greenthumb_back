package com.GreenThumb.api.forum.infrastructure.repository;

import com.GreenThumb.api.forum.infrastructure.entity.MessageEntity;
import com.GreenThumb.api.user.infrastructure.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpringDataMessageRepository extends JpaRepository<MessageEntity, Long> {

    List<MessageEntity> findByThreadId(Long threadId);

    List<MessageEntity> findByUser(UserEntity user);
}
