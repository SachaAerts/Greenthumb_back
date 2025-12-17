package com.GreenThumb.api.resources.infrastructure.repository;

import com.GreenThumb.api.resources.domain.repository.LikedRepository;
import com.GreenThumb.api.resources.infrastructure.entity.LikedEntity;
import com.GreenThumb.api.resources.infrastructure.entity.LikedId;
import org.springframework.stereotype.Repository;

@Repository
public class JpaLikedRepository implements LikedRepository {
    private final SpringDataLikedRepository likedRepository;

    public JpaLikedRepository(SpringDataLikedRepository likedRepository) {
        this.likedRepository = likedRepository;
    }

    @Override
    public boolean existsById(Long userId, Long resourceId) {
        LikedId id = new LikedId(userId, resourceId);

        return likedRepository.existsById(id);
    }

    @Override
    public void deleteById(Long userId, Long resourceId) {
        LikedId id = new LikedId(userId, resourceId);

        likedRepository.deleteById(id);
    }

    @Override
    public void save(Long userId, Long resourceId) {
        LikedEntity liked = new LikedEntity(new LikedId(userId, resourceId));
        likedRepository.save(liked);
    }
}
