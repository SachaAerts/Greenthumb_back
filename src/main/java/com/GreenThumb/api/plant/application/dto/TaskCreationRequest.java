package com.GreenThumb.api.plant.application.dto;

import com.GreenThumb.api.plant.domain.enums.TaskType;

import java.time.LocalDate;

public record TaskCreationRequest(
        String title,
        String description,
        TaskType taskType,
        LocalDate endDate,
        String color,
        Boolean isRecurrent,
        Integer recurrenceFrequency
) {
}
