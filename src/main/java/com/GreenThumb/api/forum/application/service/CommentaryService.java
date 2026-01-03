package com.GreenThumb.api.forum.application.service;


import com.GreenThumb.api.forum.domain.entity.Message;
import com.GreenThumb.api.forum.domain.repository.CommentaryRepository;
import com.GreenThumb.api.forum.infrastructure.repository.SpringDataPostRepo;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CommentaryService {

    private final CommentaryRepository commentaryRepository;
    private final SpringDataPostRepo postRepo;

    public CommentaryService(CommentaryRepository commentaryRepository, SpringDataPostRepo postRepo) {
        this.commentaryRepository = commentaryRepository;
        this.postRepo = postRepo;
    }

    public Map<Message, Long> getTopThreeMessagesByLikeCount() {
        return commentaryRepository.getTopThreeLikedCommentary();
    }

    public long countPostsByUserId(Long userId) {
        return postRepo.countByUserId(userId);
    }
}
