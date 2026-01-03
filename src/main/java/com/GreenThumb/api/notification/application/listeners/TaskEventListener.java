package com.GreenThumb.api.notification.application.listeners;

import com.GreenThumb.api.notification.application.enums.NotificationType;
import com.GreenThumb.api.notification.application.service.NotificationService;
import com.GreenThumb.api.plant.application.events.TaskCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TaskEventListener {

    private final NotificationService notificationService;

    public TaskEventListener(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @ApplicationModuleListener
    public void onTaskCreated(TaskCreatedEvent event) {
        log.info("Received TaskCreatedEvent for task: {} (user: {})", event.taskId(), event.username());

        String title = "Nouvelle tâche créée";
        String message = String.format(
                "Une nouvelle tâche '%s' a été créée pour votre plante %s",
                event.taskTitle(),
                event.plantName()
        );

        try {
            notificationService.createNotification(
                    event.username(),
                    title,
                    message,
                    NotificationType.TASK_CREATED,
                    event.taskId()
            );
            log.info("Notification created and sent for task: {}", event.taskId());
        } catch (Exception e) {
            log.error("Failed to create notification for task: {}", event.taskId(), e);
        }
    }
}
