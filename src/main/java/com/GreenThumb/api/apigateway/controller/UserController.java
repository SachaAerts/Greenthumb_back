package com.GreenThumb.api.apigateway.controller;

import com.GreenThumb.api.apigateway.service.UserServiceGateway;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserServiceGateway userService;

    public UserController(UserServiceGateway userService) {
        this.userService = userService;
    }

    @GetMapping("/count")
    public ResponseEntity<?> getUserCount() {
        return ResponseEntity.ok(userService.countUsers());
    }

    @GetMapping("/{username}/plants")
    public ResponseEntity<?> getAllPlant(@PathVariable String username) throws JsonProcessingException {
        return ResponseEntity.ok(userService.getAllPlantsByUsername(username));
    }

    
}
