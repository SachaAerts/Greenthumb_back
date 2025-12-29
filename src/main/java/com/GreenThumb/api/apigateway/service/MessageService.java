package com.GreenThumb.api.apigateway.service;

import com.GreenThumb.api.apigateway.mapper.MessageMapper;
import com.GreenThumb.api.apigateway.dto.MessageDto;
import com.GreenThumb.api.forum.application.service.CommentaryService;
import com.GreenThumb.api.forum.domain.entity.Message;
import com.GreenThumb.api.user.application.service.UserService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class MessageService {

    private final CommentaryService commentaryService;
    private final UserService userService;

    public MessageService(CommentaryService commentaryService, UserService userService) {
        this.commentaryService = commentaryService;
        this.userService = userService;
    }

    public List<MessageDto> getTop3Message() {
        Map<Message, Long> top3Message = commentaryService.getTopThreeMessagesByLikeCount();
        List<MessageDto> messages = new ArrayList<>();

        for (Map.Entry<Message, Long> entry : top3Message.entrySet()) {
            Message message = entry.getKey();
            Long id_user = entry.getValue();

            String username = userService.getUsername(id_user);

            messages.add(MessageMapper.toMessageDto(message, username));
        }

        return messages;
    }
}
