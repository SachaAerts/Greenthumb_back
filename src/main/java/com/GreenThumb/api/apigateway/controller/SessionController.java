package com.GreenThumb.api.apigateway.controller;

import com.GreenThumb.api.apigateway.dto.UserConnection;
import com.GreenThumb.api.apigateway.dto.Session;
import com.GreenThumb.api.apigateway.service.SessionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseCookie;
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
    public ResponseEntity<?> postLogin(@Valid @RequestBody UserConnection request) {
        Session session = sessionService.loginRequest(request);

        ResponseCookie refreshCookie = ResponseCookie.from("refresh_cookie", session.refreshToken())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("Strict")
                .maxAge(7 * 24 * 60 * 60)
                .build();

        return ResponseEntity.ok()
                .header("Set-Cookie", refreshCookie.toString())
                .body(session.accessToken());
    }
}
