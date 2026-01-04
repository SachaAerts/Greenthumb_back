package com.GreenThumb.api.forum.infrastructure.mapper;

import com.GreenThumb.api.forum.domain.entity.Thread;
import com.GreenThumb.api.forum.infrastructure.entity.ThreadEntity;

public class ThreadMapper {

    public static Thread toDomain(ThreadEntity entity) {
        return new Thread(
                entity.getId(),
                entity.getTitle(),
                entity.getIsPinned(),
                entity.getIsLocked(),
                entity.getUser().getUsername(),
                entity.getMessages().stream()
                        .map(MessageMapper::toDomain)
                        .toList()
        );
    }
}
