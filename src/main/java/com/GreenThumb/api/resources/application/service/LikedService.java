package com.GreenThumb.api.resources.application.service;

import com.GreenThumb.api.resources.application.dto.LikedDto;
import com.GreenThumb.api.resources.domain.repository.LikedRepository;
import com.GreenThumb.api.resources.domain.repository.ResourceRepository;
import com.GreenThumb.api.resources.infrastructure.entity.LikedId;
import com.GreenThumb.api.user.application.service.UserService;
import com.GreenThumb.api.user.domain.exception.NoFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class LikedService {

    private final ResourceRepository resourceRepository;
    private final LikedRepository likedRepository;

    private final UserService userService;

    public LikedService(
            ResourceRepository resourceRepository,
            LikedRepository likedRepository,
            UserService userService
    ) {
        this.resourceRepository = resourceRepository;
        this.likedRepository = likedRepository;
        this.userService = userService;
    }

    @Transactional
    public LikedDto addLike(String slugResource, String username) throws NoFoundException {
        Long resourceId = resourceRepository.findIdBySlug(slugResource);
        Long userId = userService.getIdByUsername(username);

        return toggleLike(userId, resourceId);
    }

    private LikedDto toggleLike(Long userId, Long resourceId) {
        boolean wasLiked = likedRepository.existsById(userId, resourceId);
        LikedDto like;

        if (wasLiked) {
            likedRepository.deleteById(userId, resourceId);
            resourceRepository.decrementLikeCount(resourceId);

            int count = resourceRepository.getLikeById(resourceId);
            like = new LikedDto(count, false);
        } else {
            likedRepository.save(userId, resourceId);
            resourceRepository.incrementLikeCount(resourceId);

            int count = resourceRepository.getLikeById(resourceId);
            like = new LikedDto(count, true);
        }

        return like;
    }
}
