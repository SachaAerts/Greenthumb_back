package com.GreenThumb.api.apigateway.controller;

import com.GreenThumb.api.apigateway.dto.CreateMessageRequest;
import com.GreenThumb.api.forum.application.dto.MessageDto;
import com.GreenThumb.api.forum.application.dto.ReactionDto;
import com.GreenThumb.api.forum.application.service.ReactionService;
import com.GreenThumb.api.infrastructure.service.RedisService;
import com.GreenThumb.api.apigateway.service.TokenExtractor;
import com.GreenThumb.api.infrastructure.service.TokenService;
import com.GreenThumb.api.forum.application.dto.ChatMessageDto;
import com.GreenThumb.api.forum.application.service.ForumMessageService;
import com.GreenThumb.api.forum.domain.services.ForumMediaStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/messages")
public class MessageController {
    private final ForumMessageService forumMessageService;
    private final ForumMediaStorageService forumMediaStorageService;
    private final ReactionService reactionService;

    private final TokenService tokenService;
    private final TokenExtractor extractor;
    private final RedisService redisService;

    public MessageController(
            TokenService tokenService,
            TokenExtractor extractor,
            ForumMessageService forumMessageService,
            ForumMediaStorageService forumMediaStorageService,
            ReactionService reactionService,
            RedisService redisService,
            MessageSource messageSource) {
        this.tokenService = tokenService;
        this.extractor = extractor;
        this.forumMessageService = forumMessageService;
        this.forumMediaStorageService = forumMediaStorageService;
        this.reactionService = reactionService;
        this.redisService = redisService;
    }

    @PostMapping
    public ResponseEntity<?> saveMessage(
            @RequestBody CreateMessageRequest request,
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader
    ) {
        String username = tokenService.extractUsername(extractor.extractToken(authorizationHeader));

        ChatMessageDto messageDto = new ChatMessageDto(
                null,
                request.threadId(),
                username,
                request.text(),
                null
        );

        ChatMessageDto messageSave = forumMessageService.createAndBroadcastMessage(messageDto, username);

        redisService.delete(username);

        return ResponseEntity.ok(messageSave);
    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String >> uploadImage(
            @RequestParam("file")MultipartFile file
    ) {
        try {
            String imageUrl = forumMediaStorageService.uploadImage(file);

            return ResponseEntity.ok(Map.of(
                    "success", "true",
                    "url", imageUrl,
                    "message", "Image uploadée avec succès"
            ));
        } catch (IllegalArgumentException e) {
            log.warn("Image upload validation failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    "success", "false",
                    "error", e.getMessage()
            ));

        } catch (Exception e) {
            log.error("Image upload failed: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", "false",
                    "error", "Erreur lors de l'upload de l'image"
            ));
        }
    }

    @PostMapping("/upload-multiple")
    public ResponseEntity<Map<String, Object>> uploadMultipleImages(
            @RequestParam("files") List<MultipartFile> files
    ) {
        try {
            List<String> imageUrls = forumMediaStorageService.uploadImages(files);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "urls", imageUrls,
                    "count", imageUrls.size(),
                    "message", imageUrls.size() + " images uploadées avec succès"
            ));

        } catch (Exception e) {
            log.error("Multiple images upload failed: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "error", "Erreur lors de l'upload des images"
            ));
        }
    }

    @GetMapping("/{messageId}/reactions")
    public ResponseEntity<List<ReactionDto>> getMessagesReaction(
            @PathVariable Long messageId,
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader
    ) {
        String username = tokenService.extractUsername(extractor.extractToken(authorizationHeader));
        List<ReactionDto> reactions = reactionService.getReactionByMessage(messageId, username);

        return ResponseEntity.ok(reactions);
    }

    @GetMapping("top3like")
    public ResponseEntity<List<MessageDto>> getTopMessage() {
        return ResponseEntity.ok(forumMessageService.getTop3Message());
    }
}
