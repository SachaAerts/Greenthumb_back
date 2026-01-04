package com.GreenThumb.api.forum.application.service;

import com.GreenThumb.api.forum.application.dto.ThreadDto;
import com.GreenThumb.api.forum.domain.entity.Thread;
import com.GreenThumb.api.forum.domain.repository.ThreadRepository;
import org.springframework.stereotype.Service;

@Service
public class ThreadModuleService {

    private final ThreadRepository threadRepository;

    public ThreadModuleService(ThreadRepository threadRepository) {
        this.threadRepository = threadRepository;
    }

    public void saveThread(ThreadDto threadDto, String channel) {
        Thread thread = threadDto.toDomain();

        threadRepository.save(thread, channel);
    }
}
