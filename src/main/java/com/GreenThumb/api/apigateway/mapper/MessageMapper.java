package com.GreenThumb.api.apigateway.mapper;

import com.GreenThumb.api.apigateway.dto.MessageDto;
import com.GreenThumb.api.forum.domain.entity.Message;
import com.GreenThumb.api.forum.domain.entity.Tag;

import java.util.List;

public class MessageMapper {

    public static MessageDto toMessageDto(Message message, String author) {
        List<String> tags = message.tags().stream()
                .map(Tag::tag)
                .toList();

        return new MessageDto(tags, author, message.date().toString());
    }
}
