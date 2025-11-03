package com.GreenThumb.api.apigateway.controller;

import com.GreenThumb.api.apigateway.dto.LoginRequest;
import com.GreenThumb.api.apigateway.service.SessionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class SessionController {

    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @PostMapping("sessions")
    public ResponseEntity<?> postSessions(@Valid @RequestBody LoginRequest request) {
        sessionService.loginRequest(request);

        return ResponseEntity.ok().build();
    }
}
