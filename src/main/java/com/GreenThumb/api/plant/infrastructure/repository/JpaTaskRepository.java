package com.GreenThumb.api.plant.infrastructure.repository;

import com.GreenThumb.api.plant.domain.entity.Task;
import com.GreenThumb.api.plant.domain.enums.TaskStatus;
import com.GreenThumb.api.plant.domain.repository.TaskRepository;
import com.GreenThumb.api.plant.infrastructure.entity.TaskEntity;
import com.GreenThumb.api.plant.infrastructure.mapper.TaskMapper;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class JpaTaskRepository implements TaskRepository {
    private final SpringDataTaskRepository springDataTaskRepository;

    public JpaTaskRepository(SpringDataTaskRepository springDataTaskRepository) {
        this.springDataTaskRepository = springDataTaskRepository;
    }

    @Override
    public List<Task> findByPlantId(Long plantId) {
        return springDataTaskRepository.findByPlantId(plantId).stream()
                .map(TaskMapper::toDomain)
                .toList();
    }

    @Override
    public List<Task> findAllByUserId(Long userId) {
        return springDataTaskRepository.findAllByUserId(userId).stream()
                .map(TaskMapper::toDomain)
                .toList();
    }

    @Override
    public long countTask(Long userId) {
        return springDataTaskRepository.countByPlantUserId(userId);
    }

    @Override
    public long countPendingTasks(Long userId) {
        return springDataTaskRepository.countPendingTasksByUserId(userId);
    }

    @Override
    public List<Task> findByStatusAndEndDate(TaskStatus status, LocalDate date) {
        return springDataTaskRepository.findByStatusAndEndDate(status, date).stream()
                .map(TaskMapper::toDomain)
                .toList();
    }

    @Override
    public List<Task> findOverdueTasks(LocalDate date) {
        return springDataTaskRepository.findOverdueTasks(date).stream()
                .map(TaskMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional
    public int updateStatusToOverdue(TaskStatus currentStatus, LocalDate date) {
        return springDataTaskRepository.updateStatusToOverdue(currentStatus, date);
    }

    @Override
    public Optional<Task> findById(Long id) {
        return springDataTaskRepository.findById(id)
                .map(TaskMapper::toDomain);
    }

    @Override
    @Transactional
    public Task save(TaskEntity taskEntity) {
        TaskEntity saved = springDataTaskRepository.save(taskEntity);
        return TaskMapper.toDomain(saved);
    }

    @Override
    public List<Task> findByPlantIdAndStatus(Long plantId, TaskStatus status) {
        return springDataTaskRepository.findByPlantIdAndStatus(plantId, status).stream()
                .map(TaskMapper::toDomain)
                .toList();
    }

    @Override
    public List<Task> findCompletedRecurrentTasks(LocalDate date) {
        return springDataTaskRepository.findCompletedRecurrentTasks(date).stream()
                .map(TaskMapper::toDomain)
                .toList();
    }
}
