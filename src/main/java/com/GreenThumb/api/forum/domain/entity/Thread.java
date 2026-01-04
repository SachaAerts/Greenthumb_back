package com.GreenThumb.api.forum.domain.entity;

import java.util.List;

public record Thread(
        Long id,
        String title,
        boolean isPinned,
        boolean isLocked,
        String creator,
        List<Message> messages
) {
}
