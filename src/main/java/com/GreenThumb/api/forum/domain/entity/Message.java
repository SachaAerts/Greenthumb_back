package com.GreenThumb.api.forum.domain.entity;

import java.time.LocalDateTime;
import java.util.List;

public record Message(
        String text,
        List<Tag> tags,
        LocalDateTime date
) {
}
