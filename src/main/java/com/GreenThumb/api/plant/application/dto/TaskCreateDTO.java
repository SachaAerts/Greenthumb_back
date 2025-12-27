package com.GreenThumb.api.plant.application.dto;

import com.GreenThumb.api.plant.domain.enums.TaskType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskCreateDTO {
    private Long plantId;
    private String title;
    private String description;
    private TaskType taskType;
    private LocalDate endDate;
    private String color;
    private Boolean isRecurrent;
    private Integer recurrenceFrequency;
    private Long templateId;
}
