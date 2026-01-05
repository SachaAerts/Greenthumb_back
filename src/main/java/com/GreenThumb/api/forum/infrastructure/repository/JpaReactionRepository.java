package com.GreenThumb.api.forum.infrastructure.repository;

import com.GreenThumb.api.forum.domain.repository.ReactionRepository;
import com.GreenThumb.api.forum.infrastructure.entity.MessageEntity;
import com.GreenThumb.api.forum.infrastructure.entity.ReactionEntity;
import com.GreenThumb.api.user.domain.exception.NoFoundException;
import com.GreenThumb.api.user.infrastructure.entity.UserEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JpaReactionRepository implements ReactionRepository {

    private final SpringDataReactionRepository reactionRepository;

    public JpaReactionRepository(SpringDataReactionRepository reactionRepository) {
        this.reactionRepository = reactionRepository;
    }

    @Override
    public ReactionEntity save(ReactionEntity reaction) {
        return reactionRepository.save(reaction);
    }

    @Override
    public Optional<ReactionEntity> findByMessageAndUserAndEmoji(MessageEntity message, UserEntity user, String emoji) {
        return reactionRepository.findByMessageAndUserAndEmoji(message, user, emoji);
    }

    @Override
    public List<ReactionEntity> findByMessage(MessageEntity message) {
        return reactionRepository.findByMessage(message);
    }

    @Override
    public void delete(ReactionEntity reaction) {
        reactionRepository.delete(reaction);
    }

    @Override
    public boolean existsByMessageAndUserAndEmoji(MessageEntity message, UserEntity user, String emoji) {
        return reactionRepository.existsByMessageAndUserAndEmoji(message, user, emoji);
    }
}
