package com.GreenThumb.api.forum.application.service;


import com.GreenThumb.api.forum.domain.entity.Message;
import com.GreenThumb.api.forum.domain.repository.CommentaryRepository;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CommentaryService {

    private final CommentaryRepository commentaryRepository;

    public CommentaryService(CommentaryRepository commentaryRepository) {
        this.commentaryRepository = commentaryRepository;
    }

    public Map<Message, Long> getTopThreeMessagesByLikeCount() {
        return commentaryRepository.getTopThreeLikedCommentary();
    }
}
