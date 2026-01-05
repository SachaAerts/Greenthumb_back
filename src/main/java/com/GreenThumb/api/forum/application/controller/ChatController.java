package com.GreenThumb.api.forum.application.controller;

import com.GreenThumb.api.forum.application.dto.ChatMessageDto;
import com.GreenThumb.api.forum.application.dto.ReactionActionDto;
import com.GreenThumb.api.forum.application.service.ForumMessageService;
import com.GreenThumb.api.forum.application.service.ReactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Slf4j
@Controller
public class ChatController {

    private final ForumMessageService forumMessageService;
    private final ReactionService reactionService;

    public ChatController(ForumMessageService forumMessageService, ReactionService reactionService) {
        this.forumMessageService = forumMessageService;
        this.reactionService = reactionService;
    }

    @MessageMapping("/forum/send")
    public void sendMessage(@Payload ChatMessageDto message, Principal principal) {
        String username = principal.getName();
        forumMessageService.createAndBroadcastMessage(message, username);
    }

    @MessageMapping("/forum/reaction")
    public void toggleReaction(@Payload ReactionActionDto reactionAction, Principal principal) {
        String username = principal.getName();
        reactionService.toggleReaction(reactionAction, username);
    }
}
