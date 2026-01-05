package com.GreenThumb.api.forum.application.dto;

public record MessageRestoredEventDto(
        String type,
        Long messageId,
        String text,
        String author,
        String reason
) {
    public static MessageRestoredEventDto create(Long messageId, String text, String author) {
        return new MessageRestoredEventDto(
                "MESSAGE_RESTORED",
                messageId,
                text,
                author,
                "Rétabli après révision humaine"
        );
    }
}
