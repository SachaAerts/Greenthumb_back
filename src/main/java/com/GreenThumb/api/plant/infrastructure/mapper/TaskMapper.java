package com.GreenThumb.api.plant.infrastructure.mapper;

import com.GreenThumb.api.plant.domain.entity.Task;
import com.GreenThumb.api.plant.infrastructure.entity.TaskEntity;

public class TaskMapper {

    public static Task toDomain(TaskEntity taskEntity) {
        return new Task(
                taskEntity.getTitle(),
                taskEntity.getDescription(),
                taskEntity.getEndDate(),
                taskEntity.getColor()
        );
    }
}
