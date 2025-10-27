package com.GreenThumb.api.forum.domain.repository;

import com.GreenThumb.api.forum.domain.entity.Message;

import java.util.Map;

public interface CommentaryRepository {
    public Map<Message, Long> getTopThreeLikedCommentary();
}
