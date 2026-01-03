package com.GreenThumb.api.forum.infrastructure.repository;

import com.GreenThumb.api.forum.domain.entity.Channel;
import com.GreenThumb.api.forum.domain.repository.ChannelRepository;
import com.GreenThumb.api.forum.infrastructure.entity.ChannelEntity;
import com.GreenThumb.api.forum.infrastructure.mapper.ChannelMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Repository
public class JpaChannelRepository implements ChannelRepository {

    private final SpringDataChannelRepo channelRepo;

    public JpaChannelRepository(SpringDataChannelRepo channelRepo) {
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

    @Override
    public boolean existChannel(String channel) {

        return channelRepo.existsByName(channel);
    }
}
