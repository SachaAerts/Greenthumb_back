package com.Greanthumb.api.forum.infrastructure.mapper;

import com.Greanthumb.api.forum.domain.entity.Message;
import com.Greanthumb.api.forum.domain.entity.Tag;
import com.Greanthumb.api.forum.infrastructure.entity.MessageEntity;


import java.util.List;

public class MessageMapper {
    public static Message toDomain(MessageEntity messageEntity) {
        List<Tag> tags = messageEntity.getTags().stream()
                .map(tag -> new Tag(
                        tag.getTag().getTag()
                ))
                .toList();
        return new Message(messageEntity.getTitle(), messageEntity.getText(), messageEntity.getLikeCount(), tags, messageEntity.getDate());
    }
}
