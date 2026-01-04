package com.GreenThumb.api.forum.application.dto;

import com.GreenThumb.api.forum.domain.entity.Thread;

import java.util.List;

public record ThreadDto (
        Long id,
        String title,
        boolean isPinned,
        boolean isLocked,
        String creator,
        List<MessageDto> messages
) {
    public static ThreadDto to(Thread thread) {
        return new ThreadDto(
                thread.id(),
                thread.title(),
                thread.isPinned(),
                thread.isLocked(),
                thread.creator(),
                thread.messages().stream()
                        .map(MessageDto::to)
                        .toList()
        );
    }

    public Thread toDomain() {
        return new Thread(
                this.id,
                this.title,
                this.isPinned,
                this.isLocked,
                this.creator,
                this.messages.stream()
                        .map(MessageDto::toDomain)
                        .toList()
        );
    }
}
