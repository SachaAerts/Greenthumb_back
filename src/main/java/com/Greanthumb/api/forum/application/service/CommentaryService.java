package com.Greanthumb.api.forum.application.service;


import com.Greanthumb.api.forum.domain.entity.Message;
import com.Greanthumb.api.forum.domain.repository.CommentaryRepository;
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
