package com.GreenThumb.api.plant.application.listeners;

import com.GreenThumb.api.plant.domain.events.TaskDueEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * LISTENER TEMPORAIRE pour tester les événements TaskDueEvent
 *
 * ⚠️ À SUPPRIMER une fois le module NOTIFICATION implémenté ⚠️
 *
 * Ce listener simule ce que fera le module NOTIFICATION :
 * - Recevoir l'événement TaskDueEvent
 * - Envoyer une notification à l'utilisateur
 */
@Slf4j
@Component
public class TemporaryTaskDueLogger {

    @Async
    @EventListener
    public void handleTaskDue(TaskDueEvent event) {
        log.info("🔔 [NOTIFICATION] Task due today!");
        log.info("   → Task: {} (ID: {})", event.getTaskTitle(), event.getTaskId());
        log.info("   → Plant: {}", event.getPlantName());
        log.info("   → Type: {}", event.getTaskType());
        log.info("   → Due Date: {}", event.getDueDate());
        log.info("   → User ID: {}", event.getUserId());

        // En production, ici on enverrait :
        // - Email
        // - Push notification
        // - Notification in-app
    }
}
