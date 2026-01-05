package com.GreenThumb.api.forum.infrastructure.repository;

import com.GreenThumb.api.forum.domain.repository.PostRepository;
import org.springframework.stereotype.Repository;

@Repository
public class JpaPostRepository implements PostRepository {

    private final SpringDataPostRepo springDataPostRepo;

    public JpaPostRepository(SpringDataPostRepo springDataPostRepo) {
        this.springDataPostRepo = springDataPostRepo;
    }

    @Override
    public long countByUserId(Long userId) {
        return springDataPostRepo.countByUserId(userId);
    }
}

