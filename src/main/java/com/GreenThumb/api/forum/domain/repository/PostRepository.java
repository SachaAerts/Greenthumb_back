package com.GreenThumb.api.forum.domain.repository;

public interface PostRepository {
    long countByUserId(Long userId);
}

