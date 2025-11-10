package com.GreenThumb.api.apigateway.controller;

import com.GreenThumb.api.apigateway.dto.user.UserConnection;
import com.GreenThumb.api.apigateway.dto.Session;
import com.GreenThumb.api.apigateway.service.AuthenticationService;
import com.GreenThumb.api.user.application.dto.UserRegister;
import com.GreenThumb.api.apigateway.service.SessionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class SessionController {

    private final SessionService sessionService;

    private final AuthenticationService authenticationService;

    public SessionController(SessionService sessionService,  AuthenticationService authenticationService) {
        this.sessionService = sessionService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/sessions")
    public ResponseEntity<?> postLogin(@Valid @RequestBody UserConnection request) {
        System.out.println(request.login() + " " + request.password());
        Session session = sessionService.sessionLoginRequest(request);

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

    @PostMapping("register")
    public ResponseEntity<?> postRegister(@Valid @RequestBody UserRegister request) {
        authenticationService.registerRequest(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }
}
