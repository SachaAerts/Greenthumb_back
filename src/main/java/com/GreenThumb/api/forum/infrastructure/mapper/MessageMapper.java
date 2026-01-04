package com.GreenThumb.api.forum.infrastructure.mapper;

import com.GreenThumb.api.forum.domain.entity.Message;
import com.GreenThumb.api.forum.infrastructure.entity.MediaEntity;
import com.GreenThumb.api.forum.infrastructure.entity.MessageEntity;

public class MessageMapper {
    public static Message toDomain(MessageEntity messageEntity) {
        return new Message(
                messageEntity.getId(),
                messageEntity.getText(),
                messageEntity.getUser().getUsername(),
                messageEntity.getDate(),
                messageEntity.getMedias().stream()
                        .map(MediaEntity::getUrl)
                        .toList()
                );
    }
}
