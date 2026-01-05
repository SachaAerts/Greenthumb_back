package com.GreenThumb.api.forum.domain.entity;

import com.GreenThumb.api.forum.application.dto.ReactionDto;

import java.time.LocalDateTime;
import java.util.List;

public record Message(
        Long id,
        String text,
        String author,
        LocalDateTime timestamp,
        List<String> mediaUrls,
        List<Reaction> reactions
) {
}
