package com.GreenThumb.api.plant.application.service;

import com.GreenThumb.api.plant.application.dto.PlantDto;
import com.GreenThumb.api.plant.application.dto.TaskDto;
import com.GreenThumb.api.plant.domain.entity.Plant;
import com.GreenThumb.api.plant.domain.entity.Task;
import com.GreenThumb.api.plant.domain.repository.PlantRepository;
import com.GreenThumb.api.plant.domain.repository.TaskRepository;
import org.h2.table.Plan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;

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

        return toDto(plantsDomain);
    }

    public Page<PlantDto> findAllByUser_username(String username, Pageable pageable) {
        Page<Plant> plants = plantRepository.findAllByUser_username(username, pageable);

        return toDto(plants);
    }

    public long countTask(Long  userId) {
        return taskRepository.countTask(userId);
    }

    private List<PlantDto> toDto(List<Plant> plantsDomain) {
        return plantsDomain.stream()
                .map(createPlantDto())
                .toList();
    }

    private Page<PlantDto> toDto(Page<Plant> plantsDomain) {
        return plantsDomain.map(createPlantDto());
    }

    private Function<Plant, PlantDto> createPlantDto() {
        return plant -> {
            Long plantId = plantRepository.findIdBySlug(plant.slug());

            List<TaskDto> tasksDto = taskRepository.findByPlantId(plantId).stream()
                    .map(task -> new TaskDto(
                            task.title(),
                            task.description(),
                            task.endDate().toString(),
                            task.color()
                    ))
                    .toList();

            return new PlantDto(
                    plant.slug(),
                    plant.scientificName(),
                    plant.commonName(),
                    plant.imageUrl(),
                    plant.description(),
                    plant.lifeCycle(),
                    plant.waterNeed(),
                    plant.lightLevel(),
                    plant.soilType(),
                    plant.soilPhMin(),
                    plant.soilPhMax(),
                    plant.temperatureMin(),
                    plant.temperatureMax(),
                    plant.humidityNeed(),
                    plant.bloomMonth(),
                    plant.petToxic(),
                    plant.humanToxic(),
                    plant.indoorFriendly(),
                    tasksDto
            );
        };
    }
}
