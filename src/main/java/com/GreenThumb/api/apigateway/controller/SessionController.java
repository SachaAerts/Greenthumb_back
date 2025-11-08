package com.GreenThumb.api.apigateway.controller;

import com.GreenThumb.api.apigateway.dto.UserConnection;
import com.GreenThumb.api.apigateway.dto.Session;
import com.GreenThumb.api.apigateway.service.SessionService;
import com.GreenThumb.api.apigateway.service.TokenService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api")
public class SessionController {

    private final SessionService sessionService;
    private final TokenService tokenService;

    public SessionController(SessionService sessionService, TokenService tokenService) {
        this.sessionService = sessionService;
        this.tokenService = tokenService;
    }

    @PostMapping("/sessions")
    public ResponseEntity<?> postLogin(@Valid @RequestBody UserConnection request) {
        System.out.println(request.login() + " " + request.password());
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

    @PostMapping("/session/refresh")
    public ResponseEntity<?> refresh(
            @CookieValue(value = "refresh_token", required = false) String refreshToken
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

        ResponseCookie refreshCookie = ResponseCookie.from("refresh_cookie", tokens.get("refresh_token"))
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("Strict")
                .maxAge(7 * 24 * 60 * 60)
                .build();

        return ResponseEntity.ok()
                .header("Set-Cookie", refreshCookie.toString())
                .body(tokens.get("access_token"));
    }
}
