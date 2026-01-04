package com.GreenThumb.api.forum.domain.repository;

import com.GreenThumb.api.forum.infrastructure.entity.MediaEntity;
import com.GreenThumb.api.forum.infrastructure.entity.MessageEntity;

import java.util.List;

public interface MediaRepository {

    MediaEntity save(MediaEntity media);

    List<MediaEntity> saveAll(List<MediaEntity> medias);

    List<MediaEntity> findByMessage(MessageEntity message);

    void deleteByMessage(MessageEntity message);
}
