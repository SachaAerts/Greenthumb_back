package com.GreenThumb.api.forum.infrastructure.repository;

import com.GreenThumb.api.forum.domain.entity.Message;
import com.GreenThumb.api.forum.domain.repository.CommentaryRepository;
import com.GreenThumb.api.forum.infrastructure.entity.MessageEntity;
import com.GreenThumb.api.forum.infrastructure.mapper.MessageMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository
public class JpaCommentaryRepository implements CommentaryRepository {

    private final SpringDataPostRepo springDataPostRepo;

    public JpaCommentaryRepository(SpringDataPostRepo springDataPostRepo) {
        this.springDataPostRepo = springDataPostRepo;
    }

    @Override
    public Map<Message, Long> getTopThreeLikedCommentary() {
        List<Object[]> results = springDataPostRepo.findTopLikedMessages(PageRequest.of(0, 3));
        Map<Message, Long> topMessages = new LinkedHashMap<>();

        for (Object[] result : results) {
            MessageEntity messageEntity = (MessageEntity) result[0];
            Long likeCount = (Long) result[1];
            Message message = MessageMapper.toDomain(messageEntity);
            topMessages.put(message, likeCount);
        }

        return topMessages;
    }
}

