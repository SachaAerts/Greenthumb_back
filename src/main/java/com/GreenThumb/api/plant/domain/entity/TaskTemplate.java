package com.GreenThumb.api.plant.domain.entity;

import com.GreenThumb.api.plant.domain.enums.TaskType;

public record TaskTemplate(
        String title,
        TaskType taskType,
        String description,
        String color,
        String waterNeed,
        String lifeCycle,
        String lightLevel,
        boolean humidityNeed,
        int baseFrequency,
        boolean isRecurrent,
        int  springAdjustment,
        int summerAdjustment,
        int autumnAdjustment,
        int winterAdjustment,
        int priority,
        boolean isActive
) {
}
