package com.GreenThumb.api.plant.application.events;

import com.GreenThumb.api.plant.domain.enums.TaskType;
import com.GreenThumb.api.plant.domain.events.PlantCreatedEvent;
import com.GreenThumb.api.plant.domain.events.TaskCompletedEvent;
import com.GreenThumb.api.plant.domain.events.TaskDueEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
public class PlantEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    public PlantEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void publishPlantCreated(
            Long plantId,
            Long userId,
            String slug,
            String scientificName,
            String commonName,
            String waterNeed,
            String lifeCycle,
            String lightLevel,
            Boolean humidityNeed,
            Boolean indoorFriendly
    ) {
        PlantCreatedEvent event = new PlantCreatedEvent(
                this,
                plantId,
                userId,
                slug,
                scientificName,
                commonName,
                waterNeed,
                lifeCycle,
                lightLevel,
                humidityNeed,
                indoorFriendly
        );

        log.info("Publishing PlantCreatedEvent: plantId={}, userId={}, name={}",
                plantId, userId, commonName);

        eventPublisher.publishEvent(event);
    }

    public void publishTaskDue(
            Long taskId,
            Long plantId,
            Long userId,
            String taskTitle,
            String plantName,
            TaskType taskType,
            LocalDate dueDate
    ) {
        TaskDueEvent event = new TaskDueEvent(
                this,
                taskId,
                plantId,
                userId,
                taskTitle,
                plantName,
                taskType,
                dueDate
        );

        log.info("Publishing TaskDueEvent: taskId={}, userId={}, plantName={}, type={}",
                taskId, userId, plantName, taskType);

        eventPublisher.publishEvent(event);
    }

    public void publishTaskCompleted(
            Long taskId,
            Long plantId,
            Long userId,
            String plantName,
            TaskType taskType,
            LocalDate completedDate,
            Boolean newTaskCreated,
            LocalDate nextTaskDate
    ) {
        TaskCompletedEvent event = new TaskCompletedEvent(
                this,
                taskId,
                plantId,
                userId,
                plantName,
                taskType,
                completedDate,
                newTaskCreated,
                nextTaskDate
        );

        log.info("Publishing TaskCompletedEvent: taskId={}, userId={}, newTaskCreated={}",
                taskId, userId, newTaskCreated);

        eventPublisher.publishEvent(event);
    }
}
