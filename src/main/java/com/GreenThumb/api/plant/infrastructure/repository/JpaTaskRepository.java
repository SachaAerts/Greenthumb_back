package com.GreenThumb.api.plant.infrastructure.repository;

import com.GreenThumb.api.plant.domain.entity.Task;
import com.GreenThumb.api.plant.domain.repository.TaskRepository;
import com.GreenThumb.api.plant.infrastructure.mapper.TaskMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class JpaTaskRepository implements TaskRepository {
    private final SpringDataTaskRepository springDataTaskRepository;

    public JpaTaskRepository(SpringDataTaskRepository springDataTaskRepository) {
        this.springDataTaskRepository = springDataTaskRepository;
    }

    public List<Task> findByPlantId(Long plantId) {
        return springDataTaskRepository.findByPlantId(plantId).stream()
                .map(TaskMapper::toDomain)
                .toList();
    }

    public long countTask(Long userId) {
        return springDataTaskRepository.countByPlantUserId(userId);
    }
}
