package com.GreenThumb.api.notification.infrastructure.mapper;

import com.GreenThumb.api.notification.application.dto.NotificationDto;
import com.GreenThumb.api.notification.domain.entity.Notification;
import com.GreenThumb.api.notification.infrastructure.entity.NotificationEntity;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    public Notification toDomain(NotificationEntity entity) {
        if (entity == null) {
            return null;
        }

        return new Notification(
                entity.getId(),
                entity.getUser().getUsername(),
                entity.getTitle(),
                entity.getMessage(),
                entity.getType(),
                entity.isRead(),
                entity.getCreatedAt(),
                entity.getRelatedEntityId()
        );
    }

    public NotificationDto toDto(NotificationEntity entity) {
        if (entity == null) {
            return null;
        }

        return new NotificationDto(
                entity.getId(),
                entity.getTitle(),
                entity.getMessage(),
                entity.getType(),
                entity.isRead(),
                entity.getCreatedAt(),
                entity.getRelatedEntityId()
        );
    }

    public NotificationDto toDto(Notification notification) {
        if (notification == null) {
            return null;
        }

        return new NotificationDto(
                notification.id(),
                notification.title(),
                notification.message(),
                notification.type(),
                notification.isRead(),
                notification.createdAt(),
                notification.relatedEntityId()
        );
    }
}
