package com.GreenThumb.api.apigateway.controller;

import com.GreenThumb.api.forum.application.dto.ModerationDecisionRequest;
import com.GreenThumb.api.forum.application.dto.ModerationDecisionResponse;
import com.GreenThumb.api.forum.application.dto.ModerationReviewDto;
import com.GreenThumb.api.forum.application.service.ModerationService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/admin/moderation")
@PreAuthorize("hasAnyRole('ADMIN', 'MODERATEUR')")
public class ModerationController {

    private final ModerationService moderationService;

    public ModerationController(ModerationService moderationService) {
        this.moderationService = moderationService;
    }

    @GetMapping("/pending")
    public ResponseEntity<Page<ModerationReviewDto>> getPendingModeration(
            @RequestParam(defaultValue = "all") String filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication
    ) {
        String moderatorUsername = authentication.getName();
        log.info("Moderator {} fetching moderation queue - filter: {}, page: {}, size: {}",
                moderatorUsername, filter, page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<ModerationReviewDto> result = moderationService.getPendingModeration(filter, pageable);

        log.info("Returning {} messages for moderation (total: {})",
                result.getNumberOfElements(), result.getTotalElements());

        return ResponseEntity.ok(result);
    }

    @GetMapping("/messages/{messageId}")
    public ResponseEntity<?> getMessageForReview(
            @PathVariable Long messageId,
            Authentication authentication
    ) {
        String moderatorUsername = authentication.getName();
        log.info("Moderator {} fetching message {} for review", moderatorUsername, messageId);

        try {
            ModerationReviewDto message = moderationService.getMessageForReview(messageId);
            return ResponseEntity.ok(message);
        } catch (IllegalArgumentException e) {
            log.warn("Message not found: {}", messageId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Message non trouvé"));
        }
    }

    @PostMapping("/{messageId}/approve")
    public ResponseEntity<?> approveAiDecision(
            @PathVariable Long messageId,
            @Valid @RequestBody(required = false) ModerationDecisionRequest request,
            Authentication authentication
    ) {
        String moderatorUsername = authentication.getName();
        String comment = request != null ? request.comment() : null;

        log.info("Moderator {} approving AI decision for message {}", moderatorUsername, messageId);

        try {
            ModerationDecisionResponse response = moderationService.approveAiDecision(
                    messageId,
                    moderatorUsername,
                    comment
            );

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("Error approving AI decision for message {}: {}", messageId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/{messageId}/reject")
    public ResponseEntity<?> rejectAiDecision(
            @PathVariable Long messageId,
            @Valid @RequestBody(required = false) ModerationDecisionRequest request,
            Authentication authentication
    ) {
        String moderatorUsername = authentication.getName();
        String comment = request != null ? request.comment() : null;

        log.info("Moderator {} rejecting AI decision for message {} (restoring message)",
                moderatorUsername, messageId);

        try {
            ModerationDecisionResponse response = moderationService.rejectAiDecision(
                    messageId,
                    moderatorUsername,
                    comment
            );

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("Error rejecting AI decision for message {}: {}", messageId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        }
    }
}
