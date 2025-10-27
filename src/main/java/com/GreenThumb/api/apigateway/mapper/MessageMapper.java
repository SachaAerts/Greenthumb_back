package com.GreenThumb.api.apigateway.mapper;

import com.GreenThumb.api.apigateway.dto.Message;
import com.GreenThumb.api.forum.domain.entity.Tag;

import java.util.List;

public class MessageMapper {

    public static Message toMessageDto(com.GreenThumb.api.forum.domain.entity.Message message, String author) {
        List<String> tags = message.tags().stream()
                .map(Tag::tag)
                .toList();

        return new Message(tags, message.title(), author, message.likeCount(), message.date().toString());
    }
}
