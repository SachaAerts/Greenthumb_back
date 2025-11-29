package com.GreenThumb.api.apigateway.controller;

import com.GreenThumb.api.apigateway.dto.ResendVerificationEmailRequest;
import com.GreenThumb.api.apigateway.dto.user.UserConnection;
import com.GreenThumb.api.apigateway.dto.Session;
import com.GreenThumb.api.apigateway.dto.VerifyEmailRequest;
import com.GreenThumb.api.apigateway.service.AuthenticationService;
import com.GreenThumb.api.user.application.dto.UserRegister;
import com.GreenThumb.api.apigateway.service.SessionService;
import com.GreenThumb.api.apigateway.service.TokenService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;

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

    @PostMapping("/sessions/verify")
    public ResponseEntity<?> verifyEmail(@Valid @RequestBody VerifyEmailRequest request) {
        sessionService.verifyEmailWithCode(request.email(), request.code());

        return ResponseEntity.ok()
                .body(Map.of(
                        "message", "Votre compte a été vérifié avec succès. Vous pouvez maintenant vous connecter.",
                        "verified", true
                ));
    }

    @PostMapping("/sessions/verify/resend")
    public ResponseEntity<?> resendVerificationEmail(@Valid @RequestBody ResendVerificationEmailRequest request) {
        sessionService.resendVerificationEmail(request.email());

        return ResponseEntity.ok()
                .body(Map.of("message", "Un nouvel email de vérification a été envoyé"));
    }

    @PostMapping("register")
    public ResponseEntity<?> postRegister(@Valid @RequestBody UserRegister request) {
        authenticationService.registerRequest(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

    @DeleteMapping("/sessions")
    public ResponseEntity<?> deleteSession(
            @CookieValue(value = "refresh_cookie", required = false) String refreshToken,
            Principal principal
    ) {
        if (refreshToken != null && tokenService.isTokenValid(refreshToken)) {
            try {
                sessionService.invalidateRefreshToken(refreshToken);
                log.info("Refresh token invalidated for user: {}", principal != null ? principal.getName() : "unknown");
            } catch (Exception e) {
                log.error("Error invalidating refresh token", e);
            }
        }

        ResponseCookie deletedCookie = ResponseCookie.from("refresh_cookie", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("Strict")
                .maxAge(0)
                .build();

        return ResponseEntity.noContent()
                .header("Set-Cookie", deletedCookie.toString())
                .build();
    }
}
