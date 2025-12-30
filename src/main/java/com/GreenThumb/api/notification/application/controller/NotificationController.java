package com.GreenThumb.api.notification.application.controller;

import com.GreenThumb.api.notification.application.dto.NotificationDto;
import com.GreenThumb.api.notification.application.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @MessageMapping("/notifications/subscribe")
    public void subscribeToNotifications(Principal principal) {
        if (principal != null) {
            String username = principal.getName();
            log.info("User subscribed to notifications: {}", username);
        }
    }

    @GetMapping("/api/notifications")
    @ResponseBody
    public ResponseEntity<List<NotificationDto>> getUserNotifications(@AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        log.debug("Fetching notifications for user: {}", username);
        List<NotificationDto> notifications = notificationService.getUserNotifications(username);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/api/notifications/unread")
    @ResponseBody
    public ResponseEntity<List<NotificationDto>> getUnreadNotifications(@AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        log.debug("Fetching unread notifications for user: {}", username);
        List<NotificationDto> notifications = notificationService.getUnreadNotifications(username);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/api/notifications/unread/count")
    @ResponseBody
    public ResponseEntity<Map<String, Long>> getUnreadCount(@AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        long count = notificationService.getUnreadCount(username);
        return ResponseEntity.ok(Map.of("count", count));
    }

    @PatchMapping("/api/notifications/{id}/read")
    @ResponseBody
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        log.debug("Marking notification as read: {}", id);
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/api/notifications/read-all")
    @ResponseBody
    public ResponseEntity<Void> markAllAsRead(@AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        log.info("Marking all notifications as read for user: {}", username);
        notificationService.markAllAsRead(username);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/api/notifications/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        log.debug("Deleting notification: {}", id);
        notificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }
}
