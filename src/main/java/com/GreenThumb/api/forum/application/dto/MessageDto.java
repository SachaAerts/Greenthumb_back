package com.GreenThumb.api.forum.application.dto;

import com.GreenThumb.api.forum.domain.entity.Message;

import java.time.LocalDateTime;
import java.util.List;

public record MessageDto(
        Long idMessage,
        String text,
        String author,
        LocalDateTime timestamp,
        List<String> mediaUrls
) {
    public static MessageDto to(Message message) {
        return new MessageDto(
                message.id(),
                message.text(),
                message.author(),
                message.timestamp(),
                message.mediaUrls()
        );
    }

    public Message toDomain() {
        return new Message(
                this.idMessage,
                this.text,
                this.author,
                this.timestamp,
                this.mediaUrls
        );
    }
}
