package com.GreenThumb.api.forum.domain.entity;

import java.time.LocalDateTime;
import java.util.List;

public record Message(
        Long id,
        String text,
        String author,
        LocalDateTime timestamp,
        List<String> mediaUrls
) {
}
