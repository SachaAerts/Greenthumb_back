package com.GreenThumb.api.forum.infrastructure.repository;

import com.GreenThumb.api.forum.infrastructure.entity.MediaEntity;
import com.GreenThumb.api.forum.infrastructure.entity.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpringDataMediaRepository extends JpaRepository<MediaEntity, Long> {
    List<MediaEntity> findByMessage(MessageEntity message);

    void deleteByMessage(MessageEntity message);
}
