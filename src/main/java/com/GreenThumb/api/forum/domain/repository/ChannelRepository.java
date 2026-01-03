package com.GreenThumb.api.forum.domain.repository;

import com.GreenThumb.api.forum.domain.entity.Channel;

import java.util.List;

public interface ChannelRepository {
    List<Channel> findAll();

    void save(Channel channel);
}
