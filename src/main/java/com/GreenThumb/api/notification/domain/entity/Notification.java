package com.GreenThumb.api.notification.domain.entity;

import com.GreenThumb.api.notification.application.enums.NotificationType;

import java.time.LocalDateTime;

public record Notification(
        Long id,
        String username,
        String title,
        String message,
        NotificationType type,
        boolean isRead,
        LocalDateTime createdAt,
        Long relatedEntityId
) {
}
