package com.GreenThumb.api.forum.domain.repository;

import com.GreenThumb.api.forum.infrastructure.entity.MessageEntity;
import com.GreenThumb.api.user.infrastructure.entity.UserEntity;

import java.util.List;

public interface MessageRepository {
    MessageEntity save(Long idThread, String text, UserEntity user);

    MessageEntity save(MessageEntity message);

    List<MessageEntity> findByThreadId(Long threadId);

    boolean existsById(Long messageId);
}
