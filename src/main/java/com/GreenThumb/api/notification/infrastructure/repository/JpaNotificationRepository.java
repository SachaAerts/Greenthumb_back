package com.GreenThumb.api.notification.infrastructure.repository;

import com.GreenThumb.api.notification.infrastructure.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaNotificationRepository extends JpaRepository<NotificationEntity, Long> {

    @Query("SELECT n FROM NotificationEntity n WHERE n.user.username = :username ORDER BY n.createdAt DESC")
    List<NotificationEntity> findByUsername(@Param("username") String username);

    @Query("SELECT n FROM NotificationEntity n WHERE n.user.username = :username AND n.isRead = false ORDER BY n.createdAt DESC")
    List<NotificationEntity> findUnreadByUsername(@Param("username") String username);

    @Modifying
    @Query("UPDATE NotificationEntity n SET n.isRead = true WHERE n.id = :notificationId")
    void markAsRead(@Param("notificationId") Long notificationId);

    @Modifying
    @Query("UPDATE NotificationEntity n SET n.isRead = true WHERE n.user.username = :username AND n.isRead = false")
    void markAllAsRead(@Param("username") String username);

    @Query("SELECT COUNT(n) FROM NotificationEntity n WHERE n.user.username = :username AND n.isRead = false")
    long countUnreadByUsername(@Param("username") String username);
}
