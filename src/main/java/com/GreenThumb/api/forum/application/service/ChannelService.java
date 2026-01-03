package com.GreenThumb.api.forum.application.service;

import com.GreenThumb.api.forum.application.dto.ChannelDto;
import com.GreenThumb.api.forum.domain.entity.Channel;
import com.GreenThumb.api.forum.domain.repository.ChannelRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChannelService {

    public final ChannelRepository channelRepository;

    public ChannelService(ChannelRepository channelRepository) {
        this.channelRepository = channelRepository;
    }

    public List<ChannelDto> findAll() {
        return channelRepository.findAll().stream()
                .map(ChannelDto::toDto)
                .toList();
    }

    public void addChannel(ChannelDto channel) {
        Channel channelDomain = channel.toDomain();

        channelRepository.save(channelDomain);
    }
}
