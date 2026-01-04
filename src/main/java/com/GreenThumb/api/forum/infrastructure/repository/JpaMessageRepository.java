package com.GreenThumb.api.forum.infrastructure.repository;

import com.GreenThumb.api.forum.domain.repository.MessageRepository;
import com.GreenThumb.api.forum.infrastructure.entity.MessageEntity;
import com.GreenThumb.api.forum.infrastructure.entity.ThreadEntity;
import com.GreenThumb.api.user.domain.exception.NoFoundException;
import com.GreenThumb.api.user.infrastructure.entity.UserEntity;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class JpaMessageRepository implements MessageRepository {

    private final SpringDataMessageRepository messageRepository;

    private final SpringDataThreadRepository threadRepository;

    public JpaMessageRepository(SpringDataMessageRepository messageRepository, SpringDataThreadRepository threadRepository) {
        this.messageRepository = messageRepository;
        this.threadRepository = threadRepository;
    }

    @Override
    public MessageEntity save(Long idThread, String text, UserEntity user) {
        ThreadEntity thread = threadRepository.findById(idThread)
                .orElseThrow(() -> new NoFoundException("Thread non trouvé"));

        MessageEntity entity = MessageEntity.builder()
                .text(text)
                .date(LocalDateTime.now())
                .user(user)
                .thread(thread)
                .build();

        return messageRepository.save(entity);
    }

    @Override
    public List<MessageEntity> findByThreadId(Long threadId) {
        return messageRepository.findByThreadId(threadId);
    }
}
