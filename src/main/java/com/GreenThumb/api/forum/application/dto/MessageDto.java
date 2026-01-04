package com.GreenThumb.api.forum.application.dto;

import com.GreenThumb.api.forum.domain.entity.Message;

import java.time.LocalDateTime;
import java.util.List;

public record MessageDto(
        Long id,
        String text,
        String author,
        LocalDateTime timestamp
) {
    public static MessageDto to(Message message) {
        return new MessageDto(
                message.id(),
                message.text(),
                message.author(),
                message.timestamp()
        );
    }

    public Message toDomain() {
        return new Message(
                this.id,
                this.text,
                this.author,
                this.timestamp
        );
    }
}
