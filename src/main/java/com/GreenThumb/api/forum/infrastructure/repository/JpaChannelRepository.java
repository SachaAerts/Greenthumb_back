package com.GreenThumb.api.forum.infrastructure.repository;

import com.GreenThumb.api.forum.domain.entity.Channel;
import com.GreenThumb.api.forum.domain.repository.ChannelRepository;
import com.GreenThumb.api.forum.infrastructure.mapper.ChannelMapper;
import org.springframework.stereotype.Repository;

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
}
