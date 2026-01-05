package com.GreenThumb.api.forum.application.service;

import com.GreenThumb.api.forum.infrastructure.entity.MessageEntity;
import com.GreenThumb.api.forum.infrastructure.repository.SpringDataMessageRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class MessageModerationUpdateService {

    private final SpringDataMessageRepository messageRepository;

    public MessageModerationUpdateService(SpringDataMessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Transactional
    public void updateModerationResult(Long messageId, boolean valid, String reason, String category) {
        log.info("Updating moderation result in transaction for message {}", messageId);

        MessageEntity message = messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("Message not found: " + messageId));

        message.setAiModerationChecked(true);
        message.setAiModerationValid(valid);
        message.setAiModerationReason(reason);
        message.setAiModerationExplanation(category);

        messageRepository.save(message);

        log.info("Moderation result persisted for message {}: valid={}", messageId, valid);
    }

    @Transactional
    public void markAsChecked(Long messageId) {
        log.info("Marking message {} as checked (error case)", messageId);

        MessageEntity message = messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("Message not found: " + messageId));

        message.setAiModerationChecked(true);
        messageRepository.save(message);
    }
}
