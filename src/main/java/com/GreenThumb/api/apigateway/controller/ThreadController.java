package com.GreenThumb.api.apigateway.controller;

import com.GreenThumb.api.forum.application.service.ForumMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/threads")
public class ThreadController {

    public final ForumMessageService forumMessageService;

    public ThreadController(ForumMessageService forumMessageService) {
        this.forumMessageService = forumMessageService;
    }
    @GetMapping("/{threadId}/messages")
    public ResponseEntity<?> getMessagesByThread(@PathVariable Long threadId) {
        log.info("Fetching messages for thread {}", threadId);

        return ResponseEntity.ok(forumMessageService.getMessagesByThread(threadId));
    }
}
