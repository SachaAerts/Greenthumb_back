package com.GreenThumb.api.plant.application.events;

import com.GreenThumb.api.plant.application.enums.TaskType;
import org.springframework.modulith.events.Externalized;

import java.time.LocalDate;

@Externalized("greenthumb.task.created::#{id()}")
public record TaskCreatedEvent(
        Long taskId,
        String username,
        String plantName,
        String taskTitle,
        String taskDescription,
        TaskType taskType,
        LocalDate endDate
) {
    public String id() {
        return taskId.toString();
    }
}
