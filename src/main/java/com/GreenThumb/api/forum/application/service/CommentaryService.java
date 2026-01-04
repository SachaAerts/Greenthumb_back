
package com.GreenThumb.api.forum.application.service;


import com.GreenThumb.api.forum.domain.entity.Message;
import com.GreenThumb.api.forum.domain.repository.CommentaryRepository;
import com.GreenThumb.api.forum.domain.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CommentaryService {

    private final CommentaryRepository commentaryRepository;
    private final PostRepository postRepository;

    public CommentaryService(CommentaryRepository commentaryRepository, PostRepository postRepository) {
        this.commentaryRepository = commentaryRepository;
        this.postRepository = postRepository;
    }

    public Map<Message, Long> getTopThreeMessagesByLikeCount() {
        return commentaryRepository.getTopThreeLikedCommentary();
    }

    public long countPostsByUserId(Long userId) {
        return postRepository.countByUserId(userId);
    }
}