package com.GreenThumb.api.plant.application.events;

import com.GreenThumb.api.plant.application.enums.TaskType;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class TaskCompletedEvent extends ApplicationEvent {

    private final Long taskId;
    private final Long plantId;
    private final Long userId;
    private final String plantName;
    private final TaskType taskType;
    private final LocalDate completedDate;
    private final Boolean newTaskCreated;
    private final LocalDate nextTaskDate;
    private final LocalDateTime triggeredAt;

    public TaskCompletedEvent(
            Object source,
            Long taskId,
            Long plantId,
            Long userId,
            String plantName,
            TaskType taskType,
            LocalDate completedDate,
            Boolean newTaskCreated,
            LocalDate nextTaskDate
    ) {
        super(source);
        this.taskId = taskId;
        this.plantId = plantId;
        this.userId = userId;
        this.plantName = plantName;
        this.taskType = taskType;
        this.completedDate = completedDate;
        this.newTaskCreated = newTaskCreated;
        this.nextTaskDate = nextTaskDate;
        this.triggeredAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "TaskCompletedEvent{" +
                "taskId=" + taskId +
                ", userId=" + userId +
                ", plantName='" + plantName + '\'' +
                ", taskType=" + taskType +
                ", completedDate=" + completedDate +
                ", newTaskCreated=" + newTaskCreated +
                ", nextTaskDate=" + nextTaskDate +
                ", triggeredAt=" + triggeredAt +
                '}';
    }
}
