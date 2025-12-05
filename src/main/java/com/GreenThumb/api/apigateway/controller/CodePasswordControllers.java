package com.GreenThumb.api.apigateway.controller;

import com.GreenThumb.api.apigateway.dto.user.CodeRequest;
import com.GreenThumb.api.apigateway.dto.user.ResetCodeRequest;
import com.GreenThumb.api.apigateway.service.RedisService;
import com.GreenThumb.api.apigateway.service.UserServiceGateway;
import com.GreenThumb.api.user.domain.exception.EmailAlreadyUsedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/codes")
public class CodePasswordControllers {
    private final UserServiceGateway userService;

    public CodePasswordControllers(UserServiceGateway userService) {
        this.userService = userService;
    }

    @PostMapping("")
    public ResponseEntity<?> resetCode(@RequestBody ResetCodeRequest request) {
        userService.resetCode(request.email());

        return ResponseEntity.noContent().build();
    }

}
