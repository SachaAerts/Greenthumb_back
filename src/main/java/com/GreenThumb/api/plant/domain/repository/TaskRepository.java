package com.GreenThumb.api.plant.domain.repository;

import com.GreenThumb.api.plant.domain.entity.Task;
import com.GreenThumb.api.plant.application.enums.TaskStatus;
import com.GreenThumb.api.plant.infrastructure.entity.TaskEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TaskRepository {

    List<Task> findByPlantId(Long plantId);

    List<Task> findAllByUserId(Long userId);

    long countTask(Long userId);

    long countPendingTasks(Long userId);

    List<Task> findByStatusAndEndDate(TaskStatus status, LocalDate date);

    List<Task> findOverdueTasks(LocalDate date);

    int updateStatusToOverdue(TaskStatus currentStatus, LocalDate date);

    Optional<Task> findById(Long id);

    Task save(TaskEntity taskEntity);

    List<Task> findByPlantIdAndStatus(Long plantId, TaskStatus status);

    List<Task> findCompletedRecurrentTasks(LocalDate date);
}
