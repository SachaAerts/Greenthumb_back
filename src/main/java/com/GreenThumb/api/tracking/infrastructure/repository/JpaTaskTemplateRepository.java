package com.GreenThumb.api.tracking.infrastructure.repository;

import com.GreenThumb.api.tracking.domain.entity.TaskTemplate;
import com.GreenThumb.api.plant.application.enums.TaskType;
import com.GreenThumb.api.tracking.domain.repository.TaskTemplateRepository;
import com.GreenThumb.api.tracking.infrastructure.mapper.TaskTemplateMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class JpaTaskTemplateRepository implements TaskTemplateRepository {

    private final SpringDataTaskTemplateRepository springDataTaskTemplateRepository;

    public JpaTaskTemplateRepository(SpringDataTaskTemplateRepository springDataTaskTemplateRepository) {
        this.springDataTaskTemplateRepository = springDataTaskTemplateRepository;
    }

    @Override
    public List<TaskTemplate> findAllActive() {
        log.debug("Finding all active task templates");

        List<TaskTemplate> templates = springDataTaskTemplateRepository.findAllActive().stream()
                .map(TaskTemplateMapper::toDomain)
                .toList();

        log.info("Found {} active templates", templates.size());

        return templates;
    }

    @Override
    public Optional<TaskTemplate> findById(Long id) {
        log.debug("Finding task template by id: {}", id);

        Optional<TaskTemplate> template = springDataTaskTemplateRepository.findById(id)
                .map(TaskTemplateMapper::toDomain);

        if (template.isPresent()) {
            log.debug("Found template with id: {}", id);
        } else {
            log.debug("No template found with id: {}", id);
        }

        return template;
    }

    @Override
    public List<TaskTemplate> findApplicableTemplates(
            String waterNeed,
            String lifeCycle,
            String lightLevel,
            Boolean humidityNeed,
            Boolean indoorFriendly
    ) {
        log.debug("Finding applicable templates for plant characteristics:");
        log.debug("  - waterNeed: {}", waterNeed);
        log.debug("  - lifeCycle: {}", lifeCycle);
        log.debug("  - lightLevel: {}", lightLevel);
        log.debug("  - humidityNeed: {}", humidityNeed);
        log.debug("  - indoorFriendly: {}", indoorFriendly);

        List<TaskTemplate> templates = springDataTaskTemplateRepository.findApplicableTemplates(
                        waterNeed,
                        lifeCycle,
                        lightLevel,
                        humidityNeed
                ).stream()
                .map(TaskTemplateMapper::toDomain)
                .toList();

        log.info("Found {} applicable templates", templates.size());

        if (log.isDebugEnabled()) {
            templates.forEach(t ->
                    log.debug("  → {} (type: {}, baseFrequency: {}j)",
                            t.title(), t.taskType(), t.baseFrequency())
            );
        }

        return templates;
    }

    @Override
    public List<TaskTemplate> findByTaskType(TaskType taskType) {
        log.debug("Finding templates by task type: {}", taskType);

        List<TaskTemplate> templates = springDataTaskTemplateRepository.findByTaskType(taskType).stream()
                .map(TaskTemplateMapper::toDomain)
                .toList();

        log.info("Found {} templates for type {}", templates.size(), taskType);

        return templates;
    }

    @Override
    public long countActive() {
        long count = springDataTaskTemplateRepository.countActive();
        log.debug("Total active templates: {}", count);
        return count;
    }
}
