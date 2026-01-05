package com.GreenThumb.api.user.application.listeners;

import com.GreenThumb.api.plant.application.events.TaskCompletedEvent;
import com.GreenThumb.api.user.domain.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TaskCompletedEventListener {

    private final UserRepository userRepository;

    public TaskCompletedEventListener(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Async
    @EventListener
    public void handleTaskCompleted(TaskCompletedEvent event) {
        log.info("Received TaskCompletedEvent: taskId={}, userId={}, plantName={}",
                event.getTaskId(), event.getUserId(), event.getPlantName());

        try {
            userRepository.incrementTasksCompleted(event.getUserId());
            log.info("✅ Incremented tasks_completed for user: {}", event.getUserId());
        } catch (Exception e) {
            log.error("Error incrementing tasks_completed for user {}: {}",
                    event.getUserId(), e.getMessage(), e);
        }
    }
}

