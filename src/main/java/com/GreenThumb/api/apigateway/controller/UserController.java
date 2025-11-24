package com.GreenThumb.api.apigateway.controller;

import com.GreenThumb.api.apigateway.service.UserServiceGateway;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


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
    public ResponseEntity<?> getAllPlant(
            @PathVariable String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) throws JsonProcessingException {
        return ResponseEntity.ok(userService.getAllPlantsByUsername(username, page, size));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMe(HttpServletRequest request) throws JsonProcessingException {
        String authHeader = request.getHeader("Authorization");

        String token = authHeader.substring(7);

        return  ResponseEntity.ok(userService.getMe(token));
    }

    
}
