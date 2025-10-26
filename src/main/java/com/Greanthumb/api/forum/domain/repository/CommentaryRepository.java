package com.Greanthumb.api.forum.domain.repository;

import com.Greanthumb.api.forum.domain.entity.Message;

import java.util.List;
import java.util.Map;

public interface CommentaryRepository {
    public Map<Message, Long> getTopThreeLikedCommentary();
}
