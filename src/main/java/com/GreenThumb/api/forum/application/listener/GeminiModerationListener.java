package com.GreenThumb.api.forum.application.listener;

import com.GreenThumb.api.forum.application.event.MessageReportedEvent;
import com.GreenThumb.api.forum.infrastructure.dto.gemini.ModerationResult;
import com.GreenThumb.api.forum.infrastructure.entity.MessageEntity;
import com.GreenThumb.api.forum.infrastructure.repository.SpringDataMessageRepository;
import com.GreenThumb.api.forum.infrastructure.service.gemini.GeminiModerationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class GeminiModerationListener {

    private final GeminiModerationService geminiModerationService;
    private final SpringDataMessageRepository messageRepository;

    public GeminiModerationListener(
            GeminiModerationService geminiModerationService,
            SpringDataMessageRepository messageRepository) {
        this.geminiModerationService = geminiModerationService;
        this.messageRepository = messageRepository;
    }

    @Async
    @EventListener
    @Transactional
    public void handleMessageReported(MessageReportedEvent event) {
        Long messageId = event.getMessageId();

        log.info("Processing AI moderation for reported message: {}", messageId);

        MessageEntity message = messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("Message not found: " + messageId));

        if (Boolean.TRUE.equals(message.getAiModerationChecked())) {
            log.info("Message {} already checked by AI, skipping", messageId);
            return;
        }

        try {
            ModerationResult result = geminiModerationService.analyzeContent(message.getText());

            log.info("AI moderation result for message {}: valid={}, category={}, reason={}",
                    messageId, result.valide(), result.categorie(), result.raison());

            message.setAiModerationChecked(true);
            message.setAiModerationValid(result.valide());
            message.setAiModerationReason(result.raison());
            message.setAiModerationExplanation(result.categorie());

            messageRepository.save(message);

            log.info("AI moderation result saved for message {}", messageId);

        } catch (Exception e) {
            log.error("Error during AI moderation for message {}: {}", messageId, e.getMessage(), e);

            message.setAiModerationChecked(true);
            messageRepository.save(message);
        }
    }
}
