package com.GreenThumb.api.apigateway.service;

import com.GreenThumb.api.user.application.dto.UserRegister;
import com.GreenThumb.api.user.application.service.EmailVerificationService;
import com.GreenThumb.api.user.application.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthenticationService - Tests unitaires")
class AuthenticationServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private EmailVerificationService emailVerificationService;

    @InjectMocks
    private AuthenticationService authenticationService;

    private UserRegister testUserRegister;
    private String frontendUrl;

    @BeforeEach
    void setUp() {
        testUserRegister = new UserRegister(
                "testuser",
                "Test",
                "User",
                "Password123!",
                "Password123!",
                "test@example.com",
                "0123456789",
                true,
                null
        );

        frontendUrl = "http://localhost:3000";
        ReflectionTestUtils.setField(authenticationService, "frontendUrl", frontendUrl);
    }

    @Test
    @DisplayName("registerRequest - Doit enregistrer l'utilisateur et envoyer l'email de vérification")
    void registerRequest_shouldRegisterUserAndSendVerificationEmail() {
        // Given
        doNothing().when(userService).postUserRegistration(testUserRegister);
        when(emailVerificationService.sendVerificationEmail(
                eq(testUserRegister.email()),
                eq(frontendUrl)
        )).thenReturn("token123");

        // When
        authenticationService.registerRequest(testUserRegister);

        // Then
        verify(userService, times(1)).postUserRegistration(testUserRegister);
        verify(emailVerificationService, times(1)).sendVerificationEmail(
                testUserRegister.email(),
                frontendUrl
        );
    }

    @Test
    @DisplayName("registerRequest - Doit enregistrer l'utilisateur même si l'envoi d'email échoue")
    void registerRequest_shouldRegisterUserEvenIfEmailFails() {
        // Given
        doNothing().when(userService).postUserRegistration(testUserRegister);
        doThrow(new RuntimeException("Email service unavailable"))
                .when(emailVerificationService)
                .sendVerificationEmail(anyString(), anyString());

        // When
        authenticationService.registerRequest(testUserRegister);

        // Then
        verify(userService, times(1)).postUserRegistration(testUserRegister);
        verify(emailVerificationService, times(1)).sendVerificationEmail(
                testUserRegister.email(),
                frontendUrl
        );
    }

    @Test
    @DisplayName("registerRequest - Doit propager les exceptions de l'enregistrement utilisateur")
    void registerRequest_shouldPropagateUserRegistrationExceptions() {
        // Given
        doThrow(new IllegalArgumentException("Email déjà utilisé"))
                .when(userService)
                .postUserRegistration(testUserRegister);

        // When & Then
        try {
            authenticationService.registerRequest(testUserRegister);
        } catch (IllegalArgumentException e) {
            // Expected
        }

        verify(userService, times(1)).postUserRegistration(testUserRegister);
        verify(emailVerificationService, never()).sendVerificationEmail(anyString(), anyString());
    }

    @Test
    @DisplayName("registerRequest - Doit utiliser le bon frontend URL")
    void registerRequest_shouldUseCorrectFrontendUrl() {
        // Given
        String customUrl = "https://production.com";
        ReflectionTestUtils.setField(authenticationService, "frontendUrl", customUrl);

        doNothing().when(userService).postUserRegistration(testUserRegister);
        when(emailVerificationService.sendVerificationEmail(anyString(), anyString())).thenReturn("token123");

        // When
        authenticationService.registerRequest(testUserRegister);

        // Then
        verify(emailVerificationService, times(1)).sendVerificationEmail(
                testUserRegister.email(),
                customUrl
        );
    }
}
