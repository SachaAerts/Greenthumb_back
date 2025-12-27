package com.GreenThumb.api.apigateway.controller;

import com.GreenThumb.api.tracking.domain.entity.TaskTemplate;
import com.GreenThumb.api.tracking.domain.repository.TaskTemplateRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/admin/task-templates")
public class TaskTemplateTestController {
    private final TaskTemplateRepository taskTemplateRepository;

    public TaskTemplateTestController(TaskTemplateRepository taskTemplateRepository) {
        this.taskTemplateRepository = taskTemplateRepository;
    }

    @GetMapping
    public ResponseEntity<List<TaskTemplate>> getAllActive() {
        log.info("📋 Fetching all active templates");

        List<TaskTemplate> templates = taskTemplateRepository.findAllActive();

        log.info("✅ Found {} active templates", templates.size());

        return ResponseEntity.ok(templates);
    }

    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> countActive() {
        log.info("🔢 Counting active templates");

        long count = taskTemplateRepository.countActive();

        log.info("✅ Total active templates: {}", count);

        return ResponseEntity.ok(Map.of("count", count));
    }

    @GetMapping("/applicable")
    public ResponseEntity<List<TaskTemplate>> findApplicable(
            @RequestParam String waterNeed,
            @RequestParam String lifeCycle,
            @RequestParam(required = false) String lightLevel,
            @RequestParam(required = false) Boolean humidityNeed
    ) {
        log.info("🔍 Finding templates for plant:");
        log.info("  - waterNeed: {}", waterNeed);
        log.info("  - lifeCycle: {}", lifeCycle);
        log.info("  - lightLevel: {}", lightLevel);
        log.info("  - humidityNeed: {}", humidityNeed);

        List<TaskTemplate> templates = taskTemplateRepository.findApplicableTemplates(
                waterNeed,
                lifeCycle,
                lightLevel,
                humidityNeed,
                true
        );

        log.info("✅ Found {} applicable templates:", templates.size());
        templates.forEach(t ->
                log.info("  → {} ({})", t.title(), t.taskType())
        );

        return ResponseEntity.ok(templates);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskTemplate> getById(@PathVariable Long id) {
        log.info("🔍 Finding template by id: {}", id);

        return taskTemplateRepository.findById(id)
                .map(template -> {
                    log.info("✅ Found template: {}", template.title());
                    return ResponseEntity.ok(template);
                })
                .orElseGet(() -> {
                    log.warn("❌ Template not found with id: {}", id);
                    return ResponseEntity.notFound().build();

                });
    }
}
