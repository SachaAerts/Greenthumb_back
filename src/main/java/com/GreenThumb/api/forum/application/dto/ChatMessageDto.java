package com.GreenThumb.api.forum.application.dto;

import java.time.LocalDateTime;

public record ChatMessageDto(
        Long idMessage,
        Long idThread,
        String username,
        String text,
        LocalDateTime timestamp

) {
}
