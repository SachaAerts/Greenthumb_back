package com.GreenThumb.api.notification.application.service;

import com.GreenThumb.api.notification.application.dto.NotificationDto;
import com.GreenThumb.api.notification.application.enums.NotificationType;
import com.GreenThumb.api.notification.domain.entity.Notification;
import com.GreenThumb.api.notification.domain.repository.NotificationRepository;
import com.GreenThumb.api.notification.infrastructure.mapper.NotificationMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final SimpMessagingTemplate messagingTemplate;

    public NotificationService(
            NotificationRepository notificationRepository,
            NotificationMapper notificationMapper,
            SimpMessagingTemplate messagingTemplate
    ) {
        this.notificationRepository = notificationRepository;
        this.notificationMapper = notificationMapper;
        this.messagingTemplate = messagingTemplate;
    }

    public NotificationDto createNotification(
            String username,
            String title,
            String message,
            NotificationType type,
            Long relatedEntityId
    ) {
        log.info("Creating notification for user: {} - Type: {}", username, type);

        Notification notification = new Notification(
                null,
                username,
                title,
                message,
                type,
                false,
                LocalDateTime.now(),
                relatedEntityId
        );

        Notification saved = notificationRepository.save(notification);
        NotificationDto dto = notificationMapper.toDto(saved);

        sendNotificationToUser(username, dto);

        log.info("Notification created and sent to user: {}", username);
        return dto;
    }

    public void sendNotificationToUser(String username, NotificationDto notification) {
        try {
            messagingTemplate.convertAndSendToUser(
                    username,
                    "/queue/notifications",
                    notification
            );
            log.debug("Notification sent via WebSocket to user: {}", username);
        } catch (Exception e) {
            log.error("Failed to send notification via WebSocket to user: {}", username, e);
        }
    }

    public List<NotificationDto> getUserNotifications(String username) {
        return notificationRepository.findByUsername(username).stream()
                .map(notificationMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<NotificationDto> getUnreadNotifications(String username) {
        return notificationRepository.findUnreadByUsername(username).stream()
                .map(notificationMapper::toDto)
                .collect(Collectors.toList());
    }

    public void markAsRead(Long notificationId) {
        log.debug("Marking notification as read: {}", notificationId);
        notificationRepository.markAsRead(notificationId);
    }

    public void markAllAsRead(String username) {
        log.info("Marking all notifications as read for user: {}", username);
        notificationRepository.markAllAsRead(username);
    }

    public void deleteNotification(Long notificationId) {
        log.debug("Deleting notification: {}", notificationId);
        notificationRepository.deleteById(notificationId);
    }

    public long getUnreadCount(String username) {
        return notificationRepository.countUnreadByUsername(username);
    }
}
