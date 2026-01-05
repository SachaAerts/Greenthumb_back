package com.GreenThumb.api.forum.application.listener;

import com.GreenThumb.api.forum.application.dto.MessageModerationEventDto;
import com.GreenThumb.api.forum.application.event.MessageReportedEvent;
import com.GreenThumb.api.forum.infrastructure.dto.gemini.ModerationResult;
import com.GreenThumb.api.forum.infrastructure.entity.MessageEntity;
import com.GreenThumb.api.forum.infrastructure.repository.SpringDataMessageRepository;
import com.GreenThumb.api.forum.infrastructure.service.gemini.GeminiModerationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class GeminiModerationListener {

    private final GeminiModerationService geminiModerationService;
    private final SpringDataMessageRepository messageRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public GeminiModerationListener(
            GeminiModerationService geminiModerationService,
            SpringDataMessageRepository messageRepository,
            SimpMessagingTemplate messagingTemplate) {
        this.geminiModerationService = geminiModerationService;
        this.messageRepository = messageRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @Async
    @EventListener
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

            messageRepository.saveAndFlush(message);

            log.info("AI moderation result saved for message {}", messageId);

            if (!result.valide()) {
                Long threadId = message.getThread().getId();
                MessageModerationEventDto moderationEvent = MessageModerationEventDto.messageRemoved(
                        messageId,
                        result.categorie() + ": " + result.raison()
                );

                messagingTemplate.convertAndSend(
                        "/topic/forum/" + threadId,
                        moderationEvent
                );

                log.info("Message removal notification sent to thread {} for message {}", threadId, messageId);
            }

        } catch (Exception e) {
            log.error("Error during AI moderation for message {}: {}", messageId, e.getMessage(), e);

            message.setAiModerationChecked(true);
            messageRepository.saveAndFlush(message);
        }
    }
}
