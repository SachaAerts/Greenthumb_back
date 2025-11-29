package com.GreenThumb.api.apigateway.controller;

import com.GreenThumb.api.plant.application.facade.PlantFacade;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api")
public class TaskController {
    public PlantFacade  plantFacade;

    public TaskController(PlantFacade plantFacade){
        this.plantFacade = plantFacade;
    }

    @GetMapping("/tasks")
    public ResponseEntity<?> getCountTask(){
        return ResponseEntity.ok(plantFacade.countTask());
    }
}
