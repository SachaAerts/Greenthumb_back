package com.GreenThumb.api.tracking.application.services;

import com.GreenThumb.api.plant.application.dto.TaskCreationRequest;
import com.GreenThumb.api.plant.application.service.TaskManagementService;
import com.GreenThumb.api.tracking.domain.entity.TaskTemplate; //todo
import com.GreenThumb.api.plant.application.events.PlantCreatedEvent;
import com.GreenThumb.api.tracking.domain.repository.TaskTemplateRepository; // todo
import com.GreenThumb.api.tracking.domain.services.SeasonalFrequencyCalculator;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class TaskCreationService {

    private final TaskTemplateRepository taskTemplateRepository;
    private final SeasonalFrequencyCalculator frequencyCalculator;
    private final TaskManagementService taskManagementService;

    public TaskCreationService(
            TaskTemplateRepository taskTemplateRepository,
            SeasonalFrequencyCalculator frequencyCalculator,
            TaskManagementService taskManagementService
    ) {

        this.taskTemplateRepository = taskTemplateRepository;
        this.frequencyCalculator = frequencyCalculator;
        this.taskManagementService = taskManagementService;
    }

    @Transactional
    public void createTasksForNewPlant(PlantCreatedEvent event) {
        log.info("Creating automatic tasks for plant: {} (ID: {})",
                event.getCommonName(), event.getPlantId());

        List<TaskTemplate> applicableTemplates = findApplicableTemplates(event);

        if (applicableTemplates.isEmpty()) {
            log.warn("No templates found for plant: {}", event.getCommonName());
            return;
        }

        log.info("Found {} applicable templates", applicableTemplates.size());

        List<TaskCreationRequest> taskRequests = new ArrayList<>();

        for (TaskTemplate template : applicableTemplates) {
            try {
                TaskCreationRequest request = buildTaskRequest(template, event.getCommonName());
                taskRequests.add(request);

            } catch (Exception e) {
                log.error("❌ Error preparing task from template {}: {}",
                        template.taskType(), e.getMessage(), e);
            }
        }

        if (!taskRequests.isEmpty()) {
            taskManagementService.createTasks(event.getPlantId(), taskRequests);
            log.info("✅ Successfully created {} tasks for plant: {}",
                    taskRequests.size(), event.getCommonName());
        }
    }

    private List<TaskTemplate> findApplicableTemplates(PlantCreatedEvent event) {
        return taskTemplateRepository.findApplicableTemplates(
                event.getWaterNeed(),
                event.getLifeCycle(),
                event.getLightLevel(),
                event.getHumidityNeed(),
                event.getIndoorFriendly()
        );
    }

    private TaskCreationRequest buildTaskRequest(TaskTemplate template, String plantName) {
        int adjustedFrequency = frequencyCalculator.calculateAdjustedFrequency(template);

        LocalDate endDate = LocalDate.now().plusDays(adjustedFrequency);

        String title = template.title().replace("{plantName}", plantName);
        String description = template.description() != null
                ? template.description().replace("{plantName}", plantName)
                : null;

        return new TaskCreationRequest(
                title,
                description,
                template.taskType(),
                endDate,
                template.color(),
                template.isRecurrent(),
                adjustedFrequency
        );
    }
}
