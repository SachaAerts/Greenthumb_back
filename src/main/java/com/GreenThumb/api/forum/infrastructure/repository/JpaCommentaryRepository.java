package com.GreenThumb.api.forum.infrastructure.repository;

import com.GreenThumb.api.forum.domain.entity.Message;
import com.GreenThumb.api.forum.domain.repository.CommentaryRepository;
import com.GreenThumb.api.forum.infrastructure.entity.MessageEntity;
import com.GreenThumb.api.forum.infrastructure.mapper.MessageMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class JpaCommentaryRepository implements CommentaryRepository {
    private final SpringDataPostRepo postRepo;


    public JpaCommentaryRepository(SpringDataPostRepo postRepo) {
        this.postRepo = postRepo;
    }

    @Override
    public Map<Message, Long> getTopThreeLikedCommentary() {
        List<Object[]> results = postRepo.findTopLikedMessages(PageRequest.of(0,3));

        Map<Message, Long> map = new LinkedHashMap<>();

        for (Object[] row : results) {
            MessageEntity message = (MessageEntity) row[0];
            Long userId = (Long) row[1];
            Message domainMessage = MessageMapper.toDomain(message);

            map.put(domainMessage, userId);
        }

        return map;
    }
}
