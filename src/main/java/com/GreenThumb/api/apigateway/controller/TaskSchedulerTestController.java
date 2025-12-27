package com.GreenThumb.api.apigateway.controller;

import com.GreenThumb.api.plant.application.service.TaskSchedulerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
/**
 * CONTROLLER TEMPORAIRE pour tester le scheduler manuellement
 *
 * ⚠️ À SUPPRIMER en production ⚠️
 * ⚠️ À PROTÉGER avec @PreAuthorize("hasRole('ADMIN')") si gardé ⚠️
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/scheduler")
public class TaskSchedulerTestController {

    private final TaskSchedulerService schedulerService;

    public TaskSchedulerTestController(TaskSchedulerService schedulerService) {
        this.schedulerService = schedulerService;
    }

    /**
     * Déclenche manuellement la vérification des tâches dues
     *
     * POST /api/admin/scheduler/check-due-tasks
     */
    @PostMapping("/check-due-tasks")
    public ResponseEntity<Map<String, String>> triggerCheckDueTasks() {
        log.info("🔧 Manual trigger: checkDueTasks");

        schedulerService.manualCheckDueTasks();

        return ResponseEntity.ok(Map.of(
                "message", "checkDueTasks triggered successfully",
                "check", "See logs for results"
        ));
    }

    /**
     * Déclenche manuellement le marquage des tâches en retard
     *
     * POST /api/admin/scheduler/mark-overdue
     */
    @PostMapping("/mark-overdue")
    public ResponseEntity<Map<String, String>> triggerMarkOverdue() {
        log.info("🔧 Manual trigger: markOverdueTasks");

        schedulerService.manualMarkOverdueTasks();

        return ResponseEntity.ok(Map.of(
                "message", "markOverdueTasks triggered successfully",
                "check", "See logs for results"
        ));
    }
}
