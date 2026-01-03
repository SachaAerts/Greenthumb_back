package com.GreenThumb.api.forum.infrastructure.repository;

import com.GreenThumb.api.forum.domain.entity.Channel;
import com.GreenThumb.api.forum.domain.repository.ChannelRepository;
import com.GreenThumb.api.forum.infrastructure.entity.ChannelEntity;
import com.GreenThumb.api.forum.infrastructure.mapper.ChannelMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class JpaChannelRepository implements ChannelRepository {

    private final SpringDateChannelRepo channelRepo;

    public JpaChannelRepository(SpringDateChannelRepo channelRepo) {
        this.channelRepo = channelRepo;
    }

    @Override
    public List<Channel> findAll() {
        return channelRepo.findAllWithThreadOrderbyPinned().stream()
                .map(ChannelMapper::toDomain)
                .toList();
    }

    @Override
    public void save(Channel channel) {
        ChannelEntity entity = ChannelEntity.builder()
                .name(channel.name())
                .description(channel.description())
                .created_at(LocalDateTime.now())
                .build();

        channelRepo.save(entity);
    }
}
