package com.GreenThumb.api.notification.infrastructure.repository;

import com.GreenThumb.api.notification.domain.entity.Notification;
import com.GreenThumb.api.notification.domain.repository.NotificationRepository;
import com.GreenThumb.api.notification.infrastructure.entity.NotificationEntity;
import com.GreenThumb.api.notification.infrastructure.mapper.NotificationMapper;
import com.GreenThumb.api.user.infrastructure.entity.UserEntity;
import com.GreenThumb.api.user.infrastructure.repository.SpringDataUserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class NotificationRepositoryImpl implements NotificationRepository {

    private final JpaNotificationRepository jpaNotificationRepository;
    private final SpringDataUserRepository springDataUserRepository;
    private final NotificationMapper notificationMapper;

    public NotificationRepositoryImpl(
            JpaNotificationRepository jpaNotificationRepository,
            SpringDataUserRepository springDataUserRepository,
            NotificationMapper notificationMapper
    ) {
        this.jpaNotificationRepository = jpaNotificationRepository;
        this.springDataUserRepository = springDataUserRepository;
        this.notificationMapper = notificationMapper;
    }

    @Override
    @Transactional
    public Notification save(Notification notification) {
        UserEntity user = springDataUserRepository.findByUsername(notification.username())
                .orElseThrow(() -> new RuntimeException("User not found: " + notification.username()));

        NotificationEntity entity = NotificationEntity.builder()
                .user(user)
                .title(notification.title())
                .message(notification.message())
                .type(notification.type())
                .isRead(notification.isRead())
                .createdAt(notification.createdAt())
                .relatedEntityId(notification.relatedEntityId())
                .build();

        NotificationEntity saved = jpaNotificationRepository.save(entity);
        return notificationMapper.toDomain(saved);
    }

    @Override
    public Optional<Notification> findById(Long id) {
        return jpaNotificationRepository.findById(id)
                .map(notificationMapper::toDomain);
    }

    @Override
    public List<Notification> findByUsername(String username) {
        return jpaNotificationRepository.findByUsername(username).stream()
                .map(notificationMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Notification> findUnreadByUsername(String username) {
        return jpaNotificationRepository.findUnreadByUsername(username).stream()
                .map(notificationMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void markAsRead(Long notificationId) {
        jpaNotificationRepository.markAsRead(notificationId);
    }

    @Override
    @Transactional
    public void markAllAsRead(String username) {
        jpaNotificationRepository.markAllAsRead(username);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        jpaNotificationRepository.deleteById(id);
    }

    @Override
    public long countUnreadByUsername(String username) {
        return jpaNotificationRepository.countUnreadByUsername(username);
    }
}
