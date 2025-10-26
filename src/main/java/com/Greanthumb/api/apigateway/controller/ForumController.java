package com.Greanthumb.api.apigateway.controller;

import com.Greanthumb.api.apigateway.service.MessageService;
import com.Greanthumb.api.user.domain.exception.NoFoundException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/messages")
public class ForumController {

    private final MessageService messageService;

    public ForumController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("/top3like")
    public ResponseEntity<?> getTop3Like() {
        ResponseEntity<?> response = null;
        try {
            response = ResponseEntity.ok(messageService.getTop3Message());
        } catch (NoFoundException e) {
            System.out.println(e.getMessage());
            response = ResponseEntity.ok(e.getMessage());
        }

        return response;
    }
}
