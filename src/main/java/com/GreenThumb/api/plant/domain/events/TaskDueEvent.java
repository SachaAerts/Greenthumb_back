package com.GreenThumb.api.plant.domain.events;

import com.GreenThumb.api.plant.domain.enums.TaskType;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class TaskDueEvent extends ApplicationEvent {
    private final Long taskId;
    private final Long plantId;
    private final Long userId;
    private final String taskTitle;
    private final String plantName;
    private final TaskType taskType;
    private final LocalDate dueDate;
    private final LocalDateTime triggeredAt;

    public TaskDueEvent(
            Object source,
            Long taskId,
            Long plantId,
            Long userId,
            String taskTitle,
            String plantName,
            TaskType taskType,
            LocalDate dueDate
    ) {
        super(source);
        this.taskId = taskId;
        this.plantId = plantId;
        this.userId = userId;
        this.taskTitle = taskTitle;
        this.plantName = plantName;
        this.taskType = taskType;
        this.dueDate = dueDate;
        this.triggeredAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "TaskDueEvent{" +
                "taskId=" + taskId +
                ", userId=" + userId +
                ", taskTitle='" + taskTitle + '\'' +
                ", plantName='" + plantName + '\'' +
                ", taskType=" + taskType +
                ", dueDate=" + dueDate +
                ", triggeredAt=" + triggeredAt +
                '}';
    }
}
