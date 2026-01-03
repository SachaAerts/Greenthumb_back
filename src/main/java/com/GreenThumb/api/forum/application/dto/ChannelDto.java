package com.GreenThumb.api.forum.application.dto;

import com.GreenThumb.api.forum.domain.entity.Channel;

import java.util.List;

public record ChannelDto(
        String name,
        String description,
        List<ThreadDto> threads
) {

    public static ChannelDto toDto(Channel channel) {

        return new ChannelDto(
                channel.name(),
                channel.description(),
                channel.threads().stream()
                        .map(ThreadDto::to)
                        .toList()
        );
    }

    public Channel toDomain() {
        return new Channel(
                this.name,
                this.description,
                this.threads.stream()
                        .map(ThreadDto::toDomain)
                        .toList()
        );
    }
}
