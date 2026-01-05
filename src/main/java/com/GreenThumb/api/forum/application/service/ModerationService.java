package com.GreenThumb.api.forum.application.service;

import com.GreenThumb.api.forum.application.dto.*;
import com.GreenThumb.api.forum.domain.entity.ReportStatus;
import com.GreenThumb.api.forum.infrastructure.entity.MessageEntity;
import com.GreenThumb.api.forum.infrastructure.entity.ReportEntity;
import com.GreenThumb.api.forum.infrastructure.repository.SpringDataMessageRepository;
import com.GreenThumb.api.forum.infrastructure.repository.SpringDataReportRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ModerationService {

    private final SpringDataMessageRepository messageRepository;
    private final SpringDataReportRepository reportRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public ModerationService(
            SpringDataMessageRepository messageRepository,
            SpringDataReportRepository reportRepository,
            SimpMessagingTemplate messagingTemplate) {
        this.messageRepository = messageRepository;
        this.reportRepository = reportRepository;
        this.messagingTemplate = messagingTemplate;
    }

    public Page<ModerationReviewDto> getPendingModeration(String filter, Pageable pageable) {
        log.info("Fetching moderation queue with filter: {}", filter);

        List<MessageEntity> messages = switch (filter) {
            case "ai_rejected" -> messageRepository.findAll().stream()
                    .filter(m -> Boolean.FALSE.equals(m.getAiModerationValid()))
                    .toList();
            case "pending_review" -> messageRepository.findAll().stream()
                    .filter(m -> Boolean.FALSE.equals(m.getAiModerationValid()))
                    .filter(m -> {
                        List<ReportEntity> reports = reportRepository.findByMessageIdOrderByCreatedAtDesc(m.getId());
                        return reports.stream().anyMatch(r -> r.getStatus() == ReportStatus.PENDING);
                    })
                    .toList();
            default -> messageRepository.findAll().stream()
                    .filter(m -> Boolean.FALSE.equals(m.getAiModerationValid()))
                    .toList();
        };

        List<ModerationReviewDto> dtos = messages.stream()
                .map(this::buildModerationReviewDto)
                .collect(Collectors.toList());

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), dtos.size());
        List<ModerationReviewDto> pageContent = dtos.subList(start, end);

        return new PageImpl<>(pageContent, pageable, dtos.size());
    }

    public ModerationReviewDto getMessageForReview(Long messageId) {
        log.info("Fetching message {} for review", messageId);

        MessageEntity message = messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("Message not found: " + messageId));

        return buildModerationReviewDto(message);
    }

    @Transactional
    public ModerationDecisionResponse approveAiDecision(
            Long messageId,
            String moderatorUsername,
            String comment) {

        log.info("Moderator {} approving AI decision for message {}", moderatorUsername, messageId);

        MessageEntity message = messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("Message not found: " + messageId));

        List<ReportEntity> reports = reportRepository.findByMessageIdOrderByCreatedAtDesc(messageId);

        reports.forEach(report -> {
            report.setStatus(ReportStatus.RESOLVED);
            report.setReviewedBy(moderatorUsername);
            report.setReviewedAt(LocalDateTime.now());
        });

        reportRepository.saveAll(reports);
        
        reportRepository.deleteAll(reports);
        
        messageRepository.delete(message);

        log.info("AI decision approved for message {} by {} - Message and reports deleted from database",
                 messageId, moderatorUsername);

        return ModerationDecisionResponse.approved(messageId, moderatorUsername);
    }

    @Transactional
    public ModerationDecisionResponse rejectAiDecision(
            Long messageId,
            String moderatorUsername,
            String comment) {

        log.info("Moderator {} rejecting AI decision for message {} (restoring message)",
                moderatorUsername, messageId);

        MessageEntity message = messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("Message not found: " + messageId));

        List<ReportEntity> reports = reportRepository.findByMessageIdOrderByCreatedAtDesc(messageId);

        reports.forEach(report -> {
            report.setStatus(ReportStatus.DISMISSED);
            report.setReviewedBy(moderatorUsername);
            report.setReviewedAt(LocalDateTime.now());
        });
        reportRepository.saveAll(reports);

        message.setAiModerationValid(true);
        messageRepository.save(message);

        MessageRestoredEventDto event = MessageRestoredEventDto.create(
                messageId,
                message.getText(),
                message.getUser().getUsername()
        );

        messagingTemplate.convertAndSend(
                "/topic/forum/" + message.getThread().getId(),
                event
        );

        log.info("Message {} restored by moderator {} and broadcasted to thread {}",
                messageId, moderatorUsername, message.getThread().getId());

        return ModerationDecisionResponse.rejected(messageId, moderatorUsername);
    }

    private ModerationReviewDto buildModerationReviewDto(MessageEntity message) {
        List<ReportEntity> reports = reportRepository.findByMessageIdOrderByCreatedAtDesc(message.getId());

        ModerationReviewDto.AiModerationInfoDto aiInfo = new ModerationReviewDto.AiModerationInfoDto(
                message.getAiModerationValid(),
                message.getAiModerationReason(),
                message.getAiModerationExplanation()
        );

        List<ModerationReviewDto.ReportInfoDto> reportInfos = reports.stream()
                .map(r -> new ModerationReviewDto.ReportInfoDto(
                        r.getId(),
                        r.getReporterUsername(),
                        r.getReason(),
                        r.getStatus().toString(),
                        r.getCreatedAt(),
                        r.getReviewedAt(),
                        r.getReviewedBy()
                ))
                .collect(Collectors.toList());

        return new ModerationReviewDto(
                message.getId(),
                message.getThread().getId(),
                message.getThread().getTitle(),
                message.getUser().getUsername(),
                message.getText(),
                message.getCreatedAt(),
                reports.size(),
                aiInfo,
                reportInfos
        );
    }
}
