package com.GreenThumb.api.tracking.domain.repository;

import com.GreenThumb.api.tracking.domain.entity.TaskTemplate;
import com.GreenThumb.api.plant.application.enums.TaskType;

import java.util.List;
import java.util.Optional;

public interface TaskTemplateRepository {

    List<TaskTemplate> findAllActive();

    Optional<TaskTemplate> findById(Long id);

    List<TaskTemplate> findApplicableTemplates(
            String waterNeed,
            String lifeCycle,
            String lightLevel,
            Boolean humidityNeed,
            Boolean indoorFriendly
    );

    List<TaskTemplate> findByTaskType(TaskType taskType);

    long countActive();
}
