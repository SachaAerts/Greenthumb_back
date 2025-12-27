package com.GreenThumb.api.plant.domain.repository;

import com.GreenThumb.api.plant.domain.entity.TaskTemplate;
import com.GreenThumb.api.plant.domain.enums.TaskType;

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
