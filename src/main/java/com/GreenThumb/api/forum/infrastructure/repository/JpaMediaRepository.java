package com.GreenThumb.api.forum.infrastructure.repository;

import com.GreenThumb.api.forum.domain.repository.MediaRepository;
import com.GreenThumb.api.forum.infrastructure.entity.MediaEntity;
import com.GreenThumb.api.forum.infrastructure.entity.MessageEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class JpaMediaRepository implements MediaRepository {

    private final SpringDataMediaRepository mediaRepository;

    public JpaMediaRepository(SpringDataMediaRepository mediaRepository) {
        this.mediaRepository = mediaRepository;
    }

    @Override
    public MediaEntity save(MediaEntity media) {
        return mediaRepository.save(media);
    }

    @Override
    public List<MediaEntity> saveAll(List<MediaEntity> medias) {
        return mediaRepository.saveAll(medias);
    }

    @Override
    public List<MediaEntity> findByMessage(MessageEntity message) {
        return mediaRepository.findByMessage(message);
    }

    @Override
    public void deleteByMessage(MessageEntity message) {
        mediaRepository.deleteByMessage(message);
    }
}
