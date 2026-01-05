package com.GreenThumb.api.forum.domain.entity;

import java.time.LocalDateTime;

public record Reaction(
        Long idReaction,
        String emoji,
        String username,
        LocalDateTime createAt
) {

}
