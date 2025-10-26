package com.Greanthumb.api.apigateway.Mapper;

import com.Greanthumb.api.apigateway.dto.Message;
import com.Greanthumb.api.forum.domain.entity.Tag;

import java.util.ArrayList;
import java.util.List;

public class MessageMapper {

    public static Message toMessageDto(com.Greanthumb.api.forum.domain.entity.Message message, String author) {
        List<String> tags = message.tags().stream()
                .map(Tag::tag)
                .toList();

        return new Message(tags, message.title(), author, message.likeCount(), message.date().toString());
    }
}
