package com.GreenThumb.api.apigateway.controller;

import com.GreenThumb.api.user.application.dto.UserEdit;
import com.GreenThumb.api.apigateway.service.UserServiceGateway;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


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

    @GetMapping("/me")
    public ResponseEntity<?> getMe(HttpServletRequest request) throws JsonProcessingException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);

        return  ResponseEntity.ok(userService.getMe(token));
    }

    @PatchMapping("/{oldUsername}")
    public ResponseEntity<?> editUser(@PathVariable String oldUsername, @RequestBody UserEdit user) {
        try {
            System.out.println(user.avatar());
            userService.editUser(user, oldUsername);
            return ResponseEntity.ok(Map.of("message", "Profil mis à jour avec succès"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        }
    }
}
