package com.GreenThumb.api.tracking.infrastructure.mapper;

import com.GreenThumb.api.tracking.domain.entity.TaskTemplate;
import com.GreenThumb.api.plant.infrastructure.entity.TaskTemplateEntity;

public class TaskTemplateMapper {

    public static TaskTemplate toDomain(TaskTemplateEntity entity) {
        if (entity == null) {
            return null;
        }

        return new TaskTemplate(
                entity.getTitleTemplate(),
                entity.getTaskType(),
                entity.getDescriptionTemplate(),
                entity.getColor(),
                entity.getWaterNeed(),
                entity.getLifeCycle(),
                entity.getLightLevel(),
                entity.getHumidityNeed() != null ? entity.getHumidityNeed() : false,
                entity.getBaseFrequency(),
                entity.getIsRecurrent() != null ? entity.getIsRecurrent() : true,
                entity.getSpringAdjustment() != null ? entity.getSpringAdjustment() : 0,
                entity.getSummerAdjustment() != null ? entity.getSummerAdjustment() : 0,
                entity.getAutumnAdjustment() != null ? entity.getAutumnAdjustment() : 0,
                entity.getWinterAdjustment() != null ? entity.getWinterAdjustment() : 0,
                entity.getPriority() != null ? entity.getPriority() : 1,
                entity.getIsActive() != null ? entity.getIsActive() : true
        );
    }

    public static TaskTemplateEntity toEntity(TaskTemplate domain) {
        if (domain == null) {
            return null;
        }

        return TaskTemplateEntity.builder()
                .titleTemplate(domain.title())
                .taskType(domain.taskType())
                .descriptionTemplate(domain.description())
                .color(domain.color())
                .waterNeed(domain.waterNeed())
                .lifeCycle(domain.lifeCycle())
                .lightLevel(domain.lightLevel())
                .humidityNeed(domain.humidityNeed())
                .baseFrequency(domain.baseFrequency())
                .isRecurrent(domain.isRecurrent())
                .springAdjustment(domain.springAdjustment())
                .summerAdjustment(domain.summerAdjustment())
                .autumnAdjustment(domain.autumnAdjustment())
                .winterAdjustment(domain.winterAdjustment())
                .priority(domain.priority())
                .isActive(domain.isActive())
                .build();
    }
}
