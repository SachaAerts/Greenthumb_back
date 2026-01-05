package com.GreenThumb.api.forum.application.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ChatMessageDto(
        Long idMessage,
        Long idThread,
        String username,
        String text,
        LocalDateTime timestamp,
        List<String> mediaUrls
) {
    public ChatMessageDto(
            Long idMessage,
            Long idThread,
            String username,
            String text,
            LocalDateTime timestamp
    ) {
        this(idMessage, idThread, username, text, timestamp, List.of());
    }
}
