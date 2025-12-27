package com.GreenThumb.api.plant.domain.entity;

import com.GreenThumb.api.plant.domain.enums.TaskStatus;
import com.GreenThumb.api.plant.domain.enums.TaskType;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record Task(
        String title,
        String description,
        TaskType taskType,
        TaskStatus status,
        LocalDate endDate,
        String color,
        Boolean isRecurrent,
        Integer recurrenceFrequency,
        LocalDate completedDate,
        LocalDateTime createdAt,

        String plantName
) {
    public boolean isOverdue() {
        return status == TaskStatus.PENDING && endDate.isBefore(LocalDate.now());
    }

    public boolean isDueToday() {
        return status == TaskStatus.PENDING && endDate.equals(LocalDate.now());
    }

    public boolean isUpcoming() {
        return status == TaskStatus.PENDING && endDate.isAfter(LocalDate.now());
    }
}
