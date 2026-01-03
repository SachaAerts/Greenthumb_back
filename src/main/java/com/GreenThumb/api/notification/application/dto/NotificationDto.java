package com.GreenThumb.api.notification.application.dto;

import com.GreenThumb.api.notification.application.enums.NotificationType;

import java.time.LocalDateTime;

public record NotificationDto(
        Long id,
        String title,
        String message,
        NotificationType type,
        boolean isRead,
        LocalDateTime createdAt,
        Long relatedEntityId
) {
}
