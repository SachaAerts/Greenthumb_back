package com.GreenThumb.api.forum.infrastructure.repository;


import com.GreenThumb.api.forum.domain.entity.Thread;
import com.GreenThumb.api.forum.domain.repository.ThreadRepository;
import com.GreenThumb.api.forum.infrastructure.entity.ChannelEntity;
import com.GreenThumb.api.forum.infrastructure.entity.ThreadEntity;
import com.GreenThumb.api.user.domain.exception.NoFoundException;
import com.GreenThumb.api.user.domain.repository.UserRepository;
import com.GreenThumb.api.user.infrastructure.entity.UserEntity;
import org.springframework.stereotype.Repository;

@Repository
public class JpaThreadRepository implements ThreadRepository {
    private final SpringDataThreadRepository threadRepository;
    private final SpringDataChannelRepo channelRepository;
    private final UserRepository userRepository;

    public JpaThreadRepository(SpringDataThreadRepository threadRepository,
                               UserRepository userRepository,
                               SpringDataChannelRepo channelRepository
    ) {
        this.threadRepository = threadRepository;
        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
    }

    @Override
    public void save(Thread thread, String channel) {
        UserEntity user = userRepository.findByUsername(thread.creator());
        ChannelEntity channelEntity = channelRepository.findByName(channel)
                .orElseThrow(() -> new NoFoundException("Le channel n'a pas été trouvé"));

        ThreadEntity threadEntity = ThreadEntity.builder()
                .title(thread.title())
                .isPinned(thread.isPinned())
                .isLocked(thread.isLocked())
                .channel(channelEntity)
                .user(user)
                .build();

        threadRepository.save(threadEntity);
    }

}