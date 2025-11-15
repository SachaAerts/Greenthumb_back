package com.GreenThumb.api.apigateway.controller;

import com.GreenThumb.api.plant.application.facade.PlantFacade;
import com.GreenThumb.api.user.application.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final PlantFacade plantModule;

    public UserController(UserService userService, PlantFacade plantModule) {
        this.userService = userService;
        this.plantModule = plantModule;
    }

    @GetMapping("/count")
    public ResponseEntity<?> getUserCount() {
        return ResponseEntity.ok(userService.countUsers());
    }

    @GetMapping("/{username}/plants")
    public ResponseEntity<?> getAllPlant(@PathVariable String username) {
        return ResponseEntity.ok(plantModule.getAllPlantsByUsername(username));
    }

    
}
