package com.GreenThumb.api.forum.application.service;

import com.GreenThumb.api.forum.application.dto.ChannelDto;
import com.GreenThumb.api.forum.domain.entity.Channel;
import com.GreenThumb.api.forum.domain.entity.Thread;
import com.GreenThumb.api.forum.domain.repository.ChannelRepository;
import com.GreenThumb.api.user.domain.exception.NoFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ChannelService {

    public final ChannelRepository channelRepository;
    private final MessageModerationFilterService moderationFilterService;

    public ChannelService(ChannelRepository channelRepository, MessageModerationFilterService moderationFilterService) {
        this.channelRepository = channelRepository;
        this.moderationFilterService = moderationFilterService;
    }

    public List<ChannelDto> findAll() {
        log.info("🔍 CHANNEL SERVICE - Fetching all channels with message filtering");

        return channelRepository.findAll().stream()
                .map(channel -> new Channel(
                        channel.name(),
                        channel.description(),
                        channel.threads().stream()
                                .map(thread -> new Thread(
                                        thread.id(),
                                        thread.title(),
                                        thread.isPinned(),
                                        thread.isLocked(),
                                        thread.creator(),
                                        thread.messages().stream()
                                                .filter(moderationFilterService::isMessageVisible)
                                                .toList()
                                ))
                                .toList()
                ))
                .map(ChannelDto::toDto)
                .toList();
    }

    public void addChannel(ChannelDto channel) {
        Channel channelDomain = channel.toDomain();

        channelRepository.save(channelDomain);
    }

    public boolean existChannel(String channel) {
        return !channel.isEmpty() && channelRepository.existChannel(channel);
    }
}
