package com.GreenThumb.api.forum.domain.repository;

import com.GreenThumb.api.forum.infrastructure.entity.MessageEntity;
import com.GreenThumb.api.forum.infrastructure.entity.ReactionEntity;
import com.GreenThumb.api.user.infrastructure.entity.UserEntity;

import java.util.List;
import java.util.Optional;

public interface ReactionRepository {

    ReactionEntity save(ReactionEntity reaction);

    Optional<ReactionEntity> findByMessageAndUserAndEmoji(
            MessageEntity message,
            UserEntity user,
            String emoji
    );

    List<ReactionEntity> findByMessage(MessageEntity message);

    void delete(ReactionEntity reaction);

    boolean existsByMessageAndUserAndEmoji(
            MessageEntity message,
            UserEntity user,
            String emoji
    );
}
