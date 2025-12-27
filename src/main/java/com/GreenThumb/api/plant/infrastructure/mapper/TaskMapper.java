package com.GreenThumb.api.plant.infrastructure.mapper;

import com.GreenThumb.api.plant.application.dto.TaskDto;
import com.GreenThumb.api.plant.domain.entity.Task;
import com.GreenThumb.api.plant.infrastructure.entity.TaskEntity;

public class TaskMapper {

    public static Task toDomain(TaskEntity taskEntity) {
        return new Task(
                taskEntity.getTitle(),
                taskEntity.getDescription(),
                taskEntity.getTaskType(),
                taskEntity.getStatus(),
                taskEntity.getEndDate(),
                taskEntity.getColor(),
                taskEntity.getIsRecurrent(),
                taskEntity.getRecurrenceFrequency(),
                taskEntity.getCompletedDate(),
                taskEntity.getCreatedAt(),
                taskEntity.getPlant() != null ? taskEntity.getPlant().getCommonName() : null
        );
    }

    public static TaskDto toDto(Task task) {
        if (task == null) {
            return null;
        }

        return new TaskDto(
                task.title(),
                task.description(),
                task.taskType(),
                task.taskType().getDisplayName(),
                task.status(),
                task.status().getDisplayName(),
                task.endDate() != null ? task.endDate().toString() : null,
                task.color(),
                task.isRecurrent(),
                task.recurrenceFrequency(),
                task.completedDate() != null ? task.completedDate().toString() : null,
                task.createdAt() != null ? task.createdAt().toString() : null,
                task.isOverdue(),
                task.isDueToday(),
                task.plantName()
        );
    }

    public static TaskDto entityToDto(TaskEntity taskEntity) {
        if (taskEntity == null) {
            return null;
        }

        Task task = toDomain(taskEntity);
        return toDto(task);
    }

}
