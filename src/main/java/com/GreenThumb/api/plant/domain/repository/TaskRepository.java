package com.GreenThumb.api.plant.domain.repository;

import com.GreenThumb.api.plant.domain.entity.Task;

import java.util.List;

public interface TaskRepository {
    List<Task> findByPlantId(Long plantId);
}
