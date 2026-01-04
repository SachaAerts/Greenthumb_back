package com.GreenThumb.api.plant.application.service;

import com.GreenThumb.api.plant.application.dto.PageResponse;
import com.GreenThumb.api.plant.application.dto.PlantDto;
import com.GreenThumb.api.plant.application.dto.TaskDto;
import com.GreenThumb.api.plant.domain.entity.Plant;
import com.GreenThumb.api.plant.domain.repository.PlantRepository;
import com.GreenThumb.api.plant.domain.repository.TaskRepository;
import com.GreenThumb.api.plant.infrastructure.mapper.PlantMapper;
import com.GreenThumb.api.plant.infrastructure.mapper.TaskMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PlantModuleService {

    private final PlantRepository plantRepository;
    private final TaskRepository taskRepository;

    public PlantModuleService(PlantRepository plantRepository, TaskRepository taskRepository) {
        this.plantRepository = plantRepository;
        this.taskRepository = taskRepository;
    }

    public List<PlantDto> findAll() {
        List<Plant> plantsDomain = plantRepository.findAll();
        return toDtoOptimized(plantsDomain);
    }

    public PageResponse<PlantDto> findAllByUser_username(String username, Pageable pageable) {
        Page<Plant> plantsPage = plantRepository.findAllByUser_username(username, pageable);

        if (plantsPage.isEmpty()) {
            return PageResponse.of(plantsPage, List.of());
        }

        List<PlantDto> plantDtos = toDtoOptimized(plantsPage.getContent());

        return PageResponse.of(plantsPage, plantDtos);
    }

    public PageResponse<PlantDto> findAllByUser_usernameAndSearch(String username, String search, Pageable pageable) {
        Page<Plant> plantsPage = plantRepository.findAllByUser_usernameAndSearch(username, search, pageable);

        if (plantsPage.isEmpty()) {
            return PageResponse.of(plantsPage, List.of());
        }

        List<PlantDto> plantDtos = toDtoOptimized(plantsPage.getContent());

        return PageResponse.of(plantsPage, plantDtos);
    }

    public long countTask(Long  userId) {
        return taskRepository.countTask(userId);
    }

    public long countPendingTasks(Long userId) {
        return taskRepository.countPendingTasks(userId);
    }

    private List<PlantDto> toDtoOptimized(List<Plant> plants) {
        if (plants.isEmpty()) {
            return List.of();
        }

        List<Long> plantIds = plants.stream()
                .map(plant -> plantRepository.findIdBySlug(plant.slug()))
                .toList();

        Map<Long, List<TaskDto>> tasksByPlantId = plantIds.stream()
                .collect(Collectors.toMap(
                        plantId -> plantId,
                        plantId -> taskRepository.findByPlantId(plantId).stream()
                                .map(TaskMapper::toDto)
                                .toList()
                ));


        return plants.stream()
                .map(plant -> {
                    Long plantId = plantRepository.findIdBySlug(plant.slug());
                    List<TaskDto> tasksDto = tasksByPlantId.getOrDefault(plantId, List.of());
                    return PlantMapper.toDto(plant, plantId, tasksDto);
                })
                .toList();
    }
}
