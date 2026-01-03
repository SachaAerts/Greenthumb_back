package com.GreenThumb.api.forum.domain.repository;

import com.GreenThumb.api.forum.domain.entity.Thread;

public interface ThreadRepository {

    void save(Thread thread, String channel);
}
