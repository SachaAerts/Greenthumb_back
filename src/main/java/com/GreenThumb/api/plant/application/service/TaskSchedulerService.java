package com.GreenThumb.api.plant.application.service;

import com.GreenThumb.api.plant.application.events.PlantEventPublisher;
import com.GreenThumb.api.plant.application.enums.TaskStatus;
import com.GreenThumb.api.plant.infrastructure.entity.TaskEntity;
import com.GreenThumb.api.plant.infrastructure.repository.SpringDataTaskRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
public class TaskSchedulerService {
    private final SpringDataTaskRepository taskRepository;
    private final PlantEventPublisher eventPublisher;

    public TaskSchedulerService(
            SpringDataTaskRepository taskRepository,
            PlantEventPublisher eventPublisher
    ) {
        this.taskRepository = taskRepository;
        this.eventPublisher = eventPublisher;
    }

    @Scheduled(cron = "0 0 6 * * *")
    @Transactional(readOnly = true)
    public void checkDueTasks() {
        LocalDate today = LocalDate.now();

        log.info("[SCHEDULER] Checking tasks due today: {}", today);

        List<TaskEntity> dueTasks = taskRepository.findByStatusAndEndDate(
                TaskStatus.PENDING,
                today
        );

        if (dueTasks.isEmpty()) {
            log.info("[SCHEDULER] No tasks due today");
            return;
        }

        log.info("[SCHEDULER] Found {} tasks due today", dueTasks.size());

        for (TaskEntity task : dueTasks) {
            try {
                publishTaskDueEvent(task);
            } catch (Exception e) {
                log.error("[SCHEDULER] Error publishing TaskDueEvent for task {}: {}",
                        task.getId(), e.getMessage(), e);
            }
        }

        log.info("[SCHEDULER] Published {} TaskDueEvent", dueTasks.size());
    }

    @Scheduled(cron = "0 0 7 * * *")
    @Transactional(readOnly = true)
    public void markOverdueTasks() {
        LocalDate yesterday = LocalDate.now().minusDays(1);

        log.info("[SCHEDULER] Marking tasks overdue (before {})", yesterday);


        int updatedCount = taskRepository.updateStatusToOverdue(
                TaskStatus.PENDING,
                yesterday
        );

        if (updatedCount > 0) {
            log.warn("[SCHEDULER] Marked {} tasks as OVERDUE", updatedCount);
        } else {
            log.info("[SCHEDULER] No overdue tasks");
        }
    }

    @Scheduled(cron = "0 0 2 1 * *")
    @Transactional(readOnly = true)
    public void cleanupOldCompletedTasks() {
        LocalDate sixMonthsAgo = LocalDate.now().minusMonths(6);

        log.info("🧹 [SCHEDULER] Cleaning up completed tasks older than {}", sixMonthsAgo);

        // Cette méthode devra être ajoutée au repository si tu veux cette fonctionnalité
        // int deletedCount = taskRepository.deleteCompletedTasksBefore(sixMonthsAgo);

        log.info("🧹 [SCHEDULER] Cleanup task scheduled (not implemented yet)");
    }

    public void manualCheckDueTasks() {
        log.info("🔧 [MANUAL] Manual trigger of checkDueTasks");
        checkDueTasks();
    }

    @Transactional
    public void manualMarkOverdueTasks() {
        log.info("🔧 [MANUAL] Manual trigger of markOverdueTasks");
        markOverdueTasks();
    }


    private void publishTaskDueEvent(TaskEntity task) {
        eventPublisher.publishTaskDue(
                task.getId(),
                task.getPlant().getId(),
                task.getPlant().getUser().getId(),
                task.getTitle(),
                task.getPlant().getCommonName(),
                task.getTaskType(),
                task.getEndDate()
        );

        log.debug("[SCHEDULER] Published TaskDueEvent for: {} (plant: {})",
                task.getTitle(), task.getPlant().getCommonName());
    }
}
