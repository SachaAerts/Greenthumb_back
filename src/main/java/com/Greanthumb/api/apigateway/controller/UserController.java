package com.Greanthumb.api.apigateway.controller;

import com.Greanthumb.api.user.application.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/count")
    public ResponseEntity<?> getUserCount() {
        System.out.println(userService.countUsers());
        return ResponseEntity.ok(userService.countUsers());
    }

}
