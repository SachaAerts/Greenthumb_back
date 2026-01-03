package com.GreenThumb.api.plant.application.service;

import com.GreenThumb.api.plant.application.dto.TaskCreationRequest;
import com.GreenThumb.api.plant.application.events.TaskCreatedEvent;
import com.GreenThumb.api.plant.domain.entity.Task;
import com.GreenThumb.api.plant.application.enums.TaskStatus;
import com.GreenThumb.api.plant.application.enums.TaskType;
import com.GreenThumb.api.plant.domain.repository.PlantRepository;
import com.GreenThumb.api.plant.domain.repository.TaskRepository;
import com.GreenThumb.api.plant.infrastructure.entity.PlantEntity;
import com.GreenThumb.api.plant.infrastructure.entity.TaskEntity;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@Service
public class TaskManagementService {
    private final TaskRepository taskRepository;
    private final PlantRepository plantRepository;
    private final ApplicationEventPublisher eventPublisher;

    public TaskManagementService(
            TaskRepository taskRepository,
            PlantRepository plantRepository,
            ApplicationEventPublisher eventPublisher
    ) {
        this.taskRepository = taskRepository;
        this.plantRepository = plantRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public Task createTask(
            Long plantId,
            String title,
            String description,
            TaskType taskType,
            LocalDate endDate,
            String color,
            Boolean isRecurrent,
            Integer recurrenceFrequency
    ) {
        log.debug("Creating task for plant {}: {} (type: {})", plantId, title, taskType);

        PlantEntity plant = plantRepository.findbyId(plantId)
                .orElseThrow(() -> new RuntimeException("Plant not found: " + plantId));

        TaskEntity taskEntity = TaskEntity.builder()
                .title(title)
                .description(description)
                .taskType(taskType)
                .status(TaskStatus.PENDING)
                .endDate(endDate)
                .color(color)
                .isRecurrent(isRecurrent != null ? isRecurrent : true)
                .recurrenceFrequency(recurrenceFrequency)
                .plant(plant)
                .createdAt(LocalDateTime.now())
                .build();

        Task savedTask = taskRepository.save(taskEntity);

        TaskCreatedEvent event = new TaskCreatedEvent(
                taskEntity.getId(),
                plant.getUser().getUsername(),
                plant.getCommonName(),
                savedTask.title(),
                savedTask.description(),
                savedTask.taskType(),
                savedTask.endDate()
        );
        eventPublisher.publishEvent(event);
        log.debug("Published TaskCreatedEvent for task: {}", taskEntity.getId());

        return savedTask;
    }

    @Transactional
    public void createTasks(Long plantId, java.util.List<TaskCreationRequest> requests) {
        PlantEntity plant = plantRepository.findbyId(plantId)
                .orElseThrow(() -> new RuntimeException("Plant not found: " + plantId));

        for (TaskCreationRequest request : requests) {
            TaskEntity taskEntity = TaskEntity.builder()
                    .title(request.title())
                    .description(request.description())
                    .taskType(request.taskType())
                    .status(TaskStatus.PENDING)
                    .endDate(request.endDate())
                    .color(request.color())
                    .isRecurrent(request.isRecurrent())
                    .recurrenceFrequency(request.recurrenceFrequency())
                    .plant(plant)
                    .createdAt(LocalDateTime.now())
                    .build();

            Task savedTask = taskRepository.save(taskEntity);

            TaskCreatedEvent event = new TaskCreatedEvent(
                    taskEntity.getId(),
                    plant.getUser().getUsername(),
                    plant.getCommonName(),
                    savedTask.title(),
                    savedTask.description(),
                    savedTask.taskType(),
                    savedTask.endDate()
            );
            eventPublisher.publishEvent(event);
        }

        log.info("✅ Created {} tasks for plant {}", requests.size(), plant.getCommonName());
    }

}
