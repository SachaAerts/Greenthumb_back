package com.GreenThumb.api.forum.application.dto;

import com.GreenThumb.api.forum.domain.entity.Message;

import java.time.LocalDateTime;
import java.util.List;

public record MessageDto(
        String text,
        List<TagDto> tags,
        LocalDateTime date
) {
    public static MessageDto to(Message message) {
        return new MessageDto(
                message.text(),
                message.tags().stream()
                        .map(TagDto::to)
                        .toList(),
                message.date()
        );
    }

    public Message toDomain() {
        return new Message(
                this.text,
                this.tags.stream()
                        .map(TagDto::toDomain)
                        .toList(),
                this.date
        );
    }
}
