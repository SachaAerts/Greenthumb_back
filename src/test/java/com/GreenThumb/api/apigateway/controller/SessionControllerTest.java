package com.GreenThumb.api.apigateway.controller;

import com.GreenThumb.api.apigateway.dto.ResendVerificationEmailRequest;
import com.GreenThumb.api.apigateway.dto.Session;
import com.GreenThumb.api.apigateway.dto.VerifyEmailRequest;
import com.GreenThumb.api.apigateway.dto.user.UserConnection;
import com.GreenThumb.api.apigateway.dto.user.UserResponse;
import com.GreenThumb.api.apigateway.service.AuthenticationService;
import com.GreenThumb.api.apigateway.service.SessionService;
import com.GreenThumb.api.apigateway.service.TokenService;
import com.GreenThumb.api.user.application.dto.UserRegister;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import jakarta.servlet.http.Cookie;

import java.security.Principal;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = SessionController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                        classes = {
                                com.GreenThumb.api.config.SecurityConfig.class,
                                com.GreenThumb.api.config.JwtAuthenticationFilter.class
                        })
        }
)
@AutoConfigureMockMvc(addFilters = false)
class SessionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SessionService sessionService;

    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("POST /api/sessions - Doit créer une session et retourner les tokens")
    void postLogin_shouldCreateSessionAndReturnTokens() throws Exception {
        // Given
        UserConnection userConnection = new UserConnection("test@example.com", "Password123!", false);
        UserResponse userResponse = new UserResponse(
            "testuser", "Test", "User", "test@example.com",
            "0123456789", "Bio", false, "USER"
        );
        Session session = new Session(userResponse, "access-token-123", "refresh-token-456");

        when(sessionService.sessionLoginRequest(any(UserConnection.class))).thenReturn(session);

        // When & Then
        mockMvc.perform(post("/api/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userConnection)))
                .andExpect(status().isOk())
                .andExpect(content().string("access-token-123"))
                .andExpect(header().exists("Set-Cookie"))
                .andExpect(header().string("Set-Cookie", org.hamcrest.Matchers.containsString("refresh_cookie=refresh-token-456")))
                .andExpect(header().string("Set-Cookie", org.hamcrest.Matchers.containsString("HttpOnly")))
                .andExpect(header().string("Set-Cookie", org.hamcrest.Matchers.containsString("Secure")))
                .andExpect(header().string("Set-Cookie", org.hamcrest.Matchers.containsString("SameSite=Strict")));

        verify(sessionService, times(1)).sessionLoginRequest(any(UserConnection.class));
    }

    @Test
    @DisplayName("POST /api/sessions/refresh - Doit rafraîchir les tokens avec un refresh token valide")
    void refresh_shouldRefreshTokensWithValidRefreshToken() throws Exception {
        // Given
        String refreshToken = "valid-refresh-token";
        Map<String, String> newTokens = Map.of(
                "access_token", "new-access-token",
                "refresh_token", "new-refresh-token"
        );

        when(tokenService.isTokenValid(refreshToken)).thenReturn(true);
        when(sessionService.checkRefreshToken(refreshToken)).thenReturn(true);
        when(sessionService.refreshToken(refreshToken)).thenReturn(newTokens);

        // When & Then
        mockMvc.perform(post("/api/sessions/refresh")
                        .cookie(new Cookie("refresh_cookie", refreshToken)))
                .andExpect(status().isOk())
                .andExpect(content().string("new-access-token"))
                .andExpect(header().exists("Set-Cookie"))
                .andExpect(header().string("Set-Cookie", org.hamcrest.Matchers.containsString("refresh_cookie=new-refresh-token")));

        verify(tokenService, times(1)).isTokenValid(refreshToken);
        verify(sessionService, times(1)).checkRefreshToken(refreshToken);
        verify(sessionService, times(1)).refreshToken(refreshToken);
    }

    @Test
    @DisplayName("POST /api/sessions/refresh - Doit retourner 401 si le refresh token est null")
    void refresh_shouldReturn401WhenRefreshTokenIsNull() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/sessions/refresh"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Missing refresh token"));

        verify(tokenService, never()).isTokenValid(anyString());
        verify(sessionService, never()).refreshToken(anyString());
    }

    @Test
    @DisplayName("POST /api/sessions/refresh - Doit retourner 401 si le refresh token est invalide")
    void refresh_shouldReturn401WhenRefreshTokenIsInvalid() throws Exception {
        // Given
        String invalidToken = "invalid-refresh-token";
        when(tokenService.isTokenValid(invalidToken)).thenReturn(false);

        // When & Then
        mockMvc.perform(post("/api/sessions/refresh")
                        .cookie(new Cookie("refresh_cookie", invalidToken)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Invalid refresh token"));

        verify(tokenService, times(1)).isTokenValid(invalidToken);
        verify(sessionService, never()).refreshToken(anyString());
    }

    @Test
    @DisplayName("POST /api/sessions/refresh - Doit retourner 401 si checkRefreshToken retourne false")
    void refresh_shouldReturn401WhenCheckRefreshTokenReturnsFalse() throws Exception {
        // Given
        String refreshToken = "valid-but-not-in-db";
        when(tokenService.isTokenValid(refreshToken)).thenReturn(true);
        when(sessionService.checkRefreshToken(refreshToken)).thenReturn(false);

        // When & Then
        mockMvc.perform(post("/api/sessions/refresh")
                        .cookie(new Cookie("refresh_cookie", refreshToken)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Invalid refresh token"));

        verify(tokenService, times(1)).isTokenValid(refreshToken);
        verify(sessionService, times(1)).checkRefreshToken(refreshToken);
        verify(sessionService, never()).refreshToken(anyString());
    }

    @Test
    @DisplayName("GET /api/sessions/check - Doit retourner 200 si l'utilisateur est authentifié")
    void checkAuthentication_shouldReturn200WhenUserIsAuthenticated() throws Exception {
        // Given
        Principal principal = () -> "test@example.com";

        // When & Then
        mockMvc.perform(get("/api/sessions/check")
                        .principal(principal))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/sessions/check - Doit retourner 401 si l'utilisateur n'est pas authentifié")
    void checkAuthentication_shouldReturn401WhenUserIsNotAuthenticated() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/sessions/check"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Unauthorized"));
    }

    @Test
    @DisplayName("POST /api/sessions/verify - Doit vérifier l'email avec le code")
    void verifyEmail_shouldVerifyEmailWithCode() throws Exception {
        // Given
        VerifyEmailRequest request = new VerifyEmailRequest("test@example.com", "123456");

        doNothing().when(sessionService).verifyEmailWithCode(anyString(), anyString());

        // When & Then
        mockMvc.perform(post("/api/sessions/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Votre compte a été vérifié avec succès. Vous pouvez maintenant vous connecter."))
                .andExpect(jsonPath("$.verified").value(true));

        verify(sessionService, times(1)).verifyEmailWithCode("test@example.com", "123456");
    }

    @Test
    @DisplayName("POST /api/sessions/verify/resend - Doit renvoyer l'email de vérification")
    void resendVerificationEmail_shouldResendVerificationEmail() throws Exception {
        // Given
        ResendVerificationEmailRequest request = new ResendVerificationEmailRequest("test@example.com");
        doNothing().when(sessionService).resendVerificationEmail(anyString());

        // When & Then
        mockMvc.perform(post("/api/sessions/verify/resend")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Un nouvel email de vérification a été envoyé"));

        verify(sessionService, times(1)).resendVerificationEmail("test@example.com");
    }

    @Test
    @DisplayName("POST /api/register - Doit créer un nouvel utilisateur")
    void postRegister_shouldCreateNewUser() throws Exception {
        // Given
        UserRegister userRegister = new UserRegister(
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
        doNothing().when(authenticationService).registerRequest(any(UserRegister.class));

        // When & Then
        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRegister)))
                .andExpect(status().isCreated());

        verify(authenticationService, times(1)).registerRequest(any(UserRegister.class));
    }

    @Test
    @DisplayName("DELETE /api/sessions - Doit supprimer la session avec un refresh token valide")
    void deleteSession_shouldDeleteSessionWithValidRefreshToken() throws Exception {
        // Given
        String refreshToken = "valid-refresh-token";
        Principal principal = () -> "test@example.com";

        when(tokenService.isTokenValid(refreshToken)).thenReturn(true);
        doNothing().when(sessionService).invalidateRefreshToken(refreshToken);

        // When & Then
        mockMvc.perform(delete("/api/sessions")
                        .cookie(new Cookie("refresh_cookie", refreshToken))
                        .principal(principal))
                .andExpect(status().isNoContent())
                .andExpect(header().exists("Set-Cookie"))
                .andExpect(header().string("Set-Cookie", org.hamcrest.Matchers.containsString("refresh_cookie=")))
                .andExpect(header().string("Set-Cookie", org.hamcrest.Matchers.containsString("Max-Age=0")));

        verify(tokenService, times(1)).isTokenValid(refreshToken);
        verify(sessionService, times(1)).invalidateRefreshToken(refreshToken);
    }

    @Test
    @DisplayName("DELETE /api/sessions - Doit supprimer la session même sans refresh token")
    void deleteSession_shouldDeleteSessionWithoutRefreshToken() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/sessions"))
                .andExpect(status().isNoContent())
                .andExpect(header().exists("Set-Cookie"))
                .andExpect(header().string("Set-Cookie", org.hamcrest.Matchers.containsString("Max-Age=0")));

        verify(tokenService, never()).isTokenValid(anyString());
        verify(sessionService, never()).invalidateRefreshToken(anyString());
    }

    @Test
    @DisplayName("DELETE /api/sessions - Doit supprimer la session même avec un token invalide")
    void deleteSession_shouldDeleteSessionWithInvalidRefreshToken() throws Exception {
        // Given
        String invalidToken = "invalid-refresh-token";
        when(tokenService.isTokenValid(invalidToken)).thenReturn(false);

        // When & Then
        mockMvc.perform(delete("/api/sessions")
                        .cookie(new Cookie("refresh_cookie", invalidToken)))
                .andExpect(status().isNoContent())
                .andExpect(header().exists("Set-Cookie"))
                .andExpect(header().string("Set-Cookie", org.hamcrest.Matchers.containsString("Max-Age=0")));

        verify(tokenService, times(1)).isTokenValid(invalidToken);
        verify(sessionService, never()).invalidateRefreshToken(anyString());
    }

    @Test
    @DisplayName("DELETE /api/sessions - Doit gérer les erreurs lors de l'invalidation du token")
    void deleteSession_shouldHandleErrorsDuringTokenInvalidation() throws Exception {
        // Given
        String refreshToken = "valid-refresh-token";
        Principal principal = () -> "test@example.com";

        when(tokenService.isTokenValid(refreshToken)).thenReturn(true);
        doThrow(new RuntimeException("Database error")).when(sessionService).invalidateRefreshToken(refreshToken);

        // When & Then
        mockMvc.perform(delete("/api/sessions")
                        .cookie(new Cookie("refresh_cookie", refreshToken))
                        .principal(principal))
                .andExpect(status().isNoContent())
                .andExpect(header().exists("Set-Cookie"))
                .andExpect(header().string("Set-Cookie", org.hamcrest.Matchers.containsString("Max-Age=0")));

        verify(tokenService, times(1)).isTokenValid(refreshToken);
        verify(sessionService, times(1)).invalidateRefreshToken(refreshToken);
    }
}
