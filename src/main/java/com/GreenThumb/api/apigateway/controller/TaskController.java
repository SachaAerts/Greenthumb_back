package com.GreenThumb.api.apigateway.controller;

import com.GreenThumb.api.plant.application.dto.TaskDto;
import com.GreenThumb.api.plant.application.service.TaskManagementService;
import com.GreenThumb.api.plant.domain.entity.Task;
import com.GreenThumb.api.plant.infrastructure.mapper.TaskMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskManagementService taskManagementService;

    public TaskController(TaskManagementService taskManagementService) {
        this.taskManagementService = taskManagementService;
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Map<String, Object>> completeTask(@PathVariable Long taskId) {
        log.info("Completing task with id: {}", taskId);

        Task completedTask = taskManagementService.completeTask(taskId);
        TaskDto taskDto = TaskMapper.toDto(completedTask);

        Map<String, Object> response = Map.of(
                "message", "Tâche complétée avec succès",
                "task", taskDto
        );

        return ResponseEntity.ok(response);
    }
}

