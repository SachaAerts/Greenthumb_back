package com.GreenThumb.api.tracking.application.listeners;

import com.GreenThumb.api.plant.domain.events.PlantCreatedEvent;
import com.GreenThumb.api.tracking.application.services.TaskCreationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PlantEventListener {
    private final TaskCreationService taskCreationService;

    public PlantEventListener(TaskCreationService taskCreationService) {
        this.taskCreationService = taskCreationService;
    }

    @Async
    @EventListener
    public void handlePlantCreated(PlantCreatedEvent event) {
        log.info("Received PlantCreatedEvent for plant: {} (ID: {})",
                event.getCommonName(), event.getPlantId());

        try {
            taskCreationService.createTasksForNewPlant(event);

            log.info("Automatic tasks created successfully for plant: {}",
                    event.getCommonName());

        } catch (Exception e) {
            log.error("Error creating automatic tasks for plant {}: {}",
                    event.getPlantId(), e.getMessage(), e);
        }
    }
}
