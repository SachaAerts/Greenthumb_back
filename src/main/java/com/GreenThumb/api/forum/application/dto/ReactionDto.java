package com.GreenThumb.api.forum.application.dto;

import java.time.LocalDateTime;

public record ReactionDto(
        Long IdReaction,
        String emoji,
        String username,
        LocalDateTime createAt
) {
}
