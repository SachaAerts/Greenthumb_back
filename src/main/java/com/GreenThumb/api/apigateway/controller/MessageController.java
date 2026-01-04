package com.GreenThumb.api.apigateway.controller;

import com.GreenThumb.api.apigateway.dto.CreateMessageRequest;
import com.GreenThumb.api.apigateway.dto.MessageDto;
import com.GreenThumb.api.apigateway.service.TokenExtractor;
import com.GreenThumb.api.apigateway.service.TokenService;
import com.GreenThumb.api.forum.application.dto.ChatMessageDto;
import com.GreenThumb.api.forum.application.service.ForumMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/messages")
public class MessageController {
    private final ForumMessageService forumMessageService;

    private final TokenService tokenService;
    private final TokenExtractor extractor;

    public MessageController(
            TokenService tokenService,
            TokenExtractor extractor,
            ForumMessageService forumMessageService
    ) {
        this.tokenService = tokenService;
        this.extractor = extractor;
        this.forumMessageService = forumMessageService;
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

        return ResponseEntity.ok(forumMessageService.createAndBroadcastMessage(messageDto, username));
    }
}
