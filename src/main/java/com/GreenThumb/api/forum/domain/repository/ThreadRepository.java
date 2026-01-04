package com.GreenThumb.api.forum.domain.repository;

import com.GreenThumb.api.forum.domain.entity.Thread;
import com.GreenThumb.api.forum.infrastructure.entity.ThreadEntity;

public interface ThreadRepository {

    void save(Thread thread, String channel);
}
