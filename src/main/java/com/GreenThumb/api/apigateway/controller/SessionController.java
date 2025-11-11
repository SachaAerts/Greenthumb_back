package com.GreenThumb.api.apigateway.controller;

import com.GreenThumb.api.apigateway.dto.user.UserConnection;
import com.GreenThumb.api.apigateway.dto.Session;
import com.GreenThumb.api.apigateway.service.AuthenticationService;
import com.GreenThumb.api.user.application.dto.UserRegister;
import com.GreenThumb.api.apigateway.service.SessionService;
import com.GreenThumb.api.apigateway.service.TokenService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api")
public class SessionController {

    private final SessionService sessionService;
    private final TokenService tokenService;

    private final AuthenticationService authenticationService;

    public SessionController(SessionService sessionService,  AuthenticationService authenticationService, TokenService tokenService) {
        this.sessionService = sessionService;
        this.authenticationService = authenticationService;
        this.tokenService = tokenService;
    }

    @PostMapping("/sessions")
    public ResponseEntity<?> postLogin(@Valid @RequestBody UserConnection request) {
        System.out.println(request.login() + " " + request.password());
        Session session = sessionService.sessionLoginRequest(request);

        return ResponseEntity.ok()
                .header("Set-Cookie", getRefreshCookie(session.refreshToken()).toString())
                .body(session.accessToken());
    }

    @PostMapping("/sessions/refresh")
    public ResponseEntity<?> refresh(
            @CookieValue(value = "refresh_cookie", required = false) String refreshToken
    ) {
        if (refreshToken == null) {
            log.warn("Refresh token is null");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Missing refresh token"));
        }
        if (!tokenService.isTokenValid(refreshToken) || !sessionService.checkRefreshToken(refreshToken)) {
            log.warn("Refresh token is invalid");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid refresh token"));
        }


        Map<String, String> tokens = sessionService.refreshToken(refreshToken);

        return ResponseEntity.ok()
                .header("Set-Cookie", getRefreshCookie(tokens.get("refresh_token")).toString())
                .body(tokens.get("access_token"));
    }

    @GetMapping("/sessions/check")
    public ResponseEntity<?> checkAuthentication(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Unauthorized"));
        }

        return ResponseEntity.ok().build();
    }

    private ResponseCookie getRefreshCookie(String token) {
        return ResponseCookie.from("refresh_cookie", token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("Strict")
                .maxAge(7 * 24 * 60 * 60)
                .build();
    }

    @PostMapping("register")
    public ResponseEntity<?> postRegister(@Valid @RequestBody UserRegister request) {
        authenticationService.registerRequest(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }
}
