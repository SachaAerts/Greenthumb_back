package com.GreenThumb.api.forum.application.controller;

import com.GreenThumb.api.forum.application.dto.ChatMessageDto;
import com.GreenThumb.api.forum.application.service.ForumMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Slf4j
@Controller
public class ChatController {

    private final ForumMessageService forumMessageService;

    public ChatController(ForumMessageService forumMessageService) {
        this.forumMessageService = forumMessageService;
    }

    @MessageMapping("/forum/send")
    public void sendMessage(@Payload ChatMessageDto message, Principal principal) {
        String username = principal.getName();
        forumMessageService.createAndBroadcastMessage(message, username);
    }
}
