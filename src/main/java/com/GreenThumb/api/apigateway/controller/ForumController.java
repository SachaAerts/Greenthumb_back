package com.GreenThumb.api.apigateway.controller;

import com.GreenThumb.api.apigateway.dto.MessageDto;
import com.GreenThumb.api.apigateway.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/messages")
public class ForumController {

    private final MessageService messageService;

    public ForumController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("/top3like")
    public ResponseEntity<List<MessageDto>> getTop3Like() {
        log.debug("Fetching top 3 most liked messages");

        List<MessageDto> messages = messageService.getTop3Message();

        if (messages.isEmpty()) {
            log.debug("No messages found, returning empty list");
            return ResponseEntity.ok(messages);
        }

        log.debug("Found {} messages", messages.size());
        return ResponseEntity.ok(messages);
    }
}
