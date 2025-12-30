package com.GreenThumb.api.notification.domain.repository;

import com.GreenThumb.api.notification.domain.entity.Notification;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository {
    Notification save(Notification notification);
    Optional<Notification> findById(Long id);
    List<Notification> findByUsername(String username);
    List<Notification> findUnreadByUsername(String username);
    void markAsRead(Long notificationId);
    void markAllAsRead(String username);
    void deleteById(Long id);
    long countUnreadByUsername(String username);
}
