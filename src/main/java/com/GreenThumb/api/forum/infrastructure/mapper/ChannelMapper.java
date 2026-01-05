package com.GreenThumb.api.forum.infrastructure.mapper;

import com.GreenThumb.api.forum.domain.entity.Channel;
import com.GreenThumb.api.forum.infrastructure.entity.ChannelEntity;

public class ChannelMapper {

    public static Channel toDomain(ChannelEntity entity) {
        return new Channel(
                entity.getName(),
                entity.getDescription(),
                entity.getThreads().stream()
                        .map(ThreadMapper::toDomain)
                        .toList()
        );
    }
}
