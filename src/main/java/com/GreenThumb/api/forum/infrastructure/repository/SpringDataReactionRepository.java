package com.GreenThumb.api.forum.infrastructure.repository;

import com.GreenThumb.api.forum.infrastructure.entity.MessageEntity;
import com.GreenThumb.api.forum.infrastructure.entity.ReactionEntity;
import com.GreenThumb.api.user.infrastructure.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SpringDataReactionRepository extends JpaRepository<ReactionEntity, Long> {
    Optional<ReactionEntity> findByMessageAndUserAndEmoji(
            MessageEntity message,
            UserEntity user,
            String emoji
    );

    List<ReactionEntity> findByMessage(MessageEntity message);

    boolean existsByMessageAndUserAndEmoji(
            MessageEntity message,
            UserEntity user,
            String emoji
    );
}
