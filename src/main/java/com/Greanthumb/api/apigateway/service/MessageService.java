package com.Greanthumb.api.apigateway.service;

import com.Greanthumb.api.apigateway.Mapper.MessageMapper;
import com.Greanthumb.api.apigateway.dto.Message;
import com.Greanthumb.api.forum.application.service.CommentaryService;
import com.Greanthumb.api.user.application.service.UserService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class MessageService {

    private CommentaryService commentaryService;
    private UserService userService;

    public MessageService(CommentaryService commentaryService, UserService userService) {
        this.commentaryService = commentaryService;
        this.userService = userService;
    }

    public List<Message> getTop3Message() {
        Map<com.Greanthumb.api.forum.domain.entity.Message, Long> top3Message = commentaryService.getTopThreeMessagesByLikeCount();
        List<Message> messages = new ArrayList<>();

        for (Map.Entry<com.Greanthumb.api.forum.domain.entity.Message, Long> entry : top3Message.entrySet()) {
            com.Greanthumb.api.forum.domain.entity.Message message = entry.getKey();
            Long id_user = entry.getValue();

            String username = userService.getUsername(id_user);

            messages.add(MessageMapper.toMessageDto(message, username));
        }

        return messages;
    }
}
