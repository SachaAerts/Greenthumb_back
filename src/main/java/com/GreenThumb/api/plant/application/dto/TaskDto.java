package com.GreenThumb.api.plant.application.dto;

import com.GreenThumb.api.plant.domain.enums.TaskStatus;
import com.GreenThumb.api.plant.domain.enums.TaskType;

public record TaskDto (
        String title,
        String description,
        TaskType taskType,
        String taskTypeDisplay,
        TaskStatus status,
        String statusDisplay,
        String endDate,
        String color,
        Boolean isRecurrent,
        Integer recurrenceFrequency,
        String completedDate,
        String createdAt,
        Boolean isOverdue,
        Boolean isDueToday,
        String plantName
){
    public TaskDto(String title, String description, String endDate, String color) {
        this(title, description, TaskType.ARROSAGE, "Arrosage",
                TaskStatus.PENDING, "En attente", endDate, color,
                true, null, null, null, false, false, null);
    }
}
