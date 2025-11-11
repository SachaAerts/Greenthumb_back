package com.GreenThumb.api.apigateway.service;

import com.GreenThumb.api.user.application.dto.UserRegister;
import com.GreenThumb.api.user.application.service.EmailVerificationService;
import com.GreenThumb.api.user.application.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthenticationService {
    private final UserService userService;
    private final EmailVerificationService emailVerificationService;

    @Value("${greenthumb.frontend.url}")
    private String frontendUrl;

    public AuthenticationService(UserService userService, EmailVerificationService emailVerificationService) {
        this.userService = userService;
        this.emailVerificationService = emailVerificationService;
    }

    public void registerRequest(UserRegister registerRequest) {
        userService.postUserRegistration(registerRequest);

        try {
            emailVerificationService.sendVerificationEmail(registerRequest.email(), frontendUrl);
            log.info("Email de vérification envoyé à {}", registerRequest.email());
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi de l'email de vérification à {}: {}",
                     registerRequest.email(), e.getMessage(), e);
        }
    }
}
