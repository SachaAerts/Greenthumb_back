package com.GreenThumb.api.apigateway.service;

import com.GreenThumb.api.apigateway.dto.Session;
import com.GreenThumb.api.apigateway.dto.user.UserConnection;
import com.GreenThumb.api.apigateway.utils.EmailValidator;
import com.GreenThumb.api.user.application.dto.UserDto;
import com.GreenThumb.api.user.application.service.EmailVerificationService;
import com.GreenThumb.api.user.application.service.UserService;
import com.GreenThumb.api.user.domain.exception.InvalidTokenException;
import com.GreenThumb.api.user.domain.exception.UserAlreadyVerifiedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SessionService - Tests unitaires")
class SessionServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private TokenService tokenService;

    @Mock
    private RedisService redisService;

    @Mock
    private EmailVerificationService emailVerificationService;

    @Mock
    private EmailValidator emailValidator;

    @InjectMocks
    private SessionService sessionService;

    private UserDto testUser;
    private String frontendUrl;

    @BeforeEach
    void setUp() {
        testUser = new UserDto(
                "testuser",
                "Test",
                "User",
                "test@example.com",
                "0123456789",
                "Test bio",
                false,
                "USER",
                "default-avatar.png"
        );

        frontendUrl = "http://localhost:3000";
        ReflectionTestUtils.setField(sessionService, "frontendUrl", frontendUrl);
        ReflectionTestUtils.setField(sessionService, "refreshTokenExpirationLong", 604800000L);
        ReflectionTestUtils.setField(sessionService, "refreshTokenExpirationShort", 86400000L);
    }

    private void setupTokenMocks() {
        when(tokenService.generateAccessToken(anyString(), anyMap())).thenReturn("access-token");
        when(tokenService.generateRefreshToken(anyString())).thenReturn("refresh-token");
    }

    @Test
    @DisplayName("sessionLoginRequest - Doit connecter l'utilisateur avec email valide")
    void sessionLoginRequest_shouldLoginWithValidEmail() {
        // Given
        setupTokenMocks();
        UserConnection loginRequest = new UserConnection("test@example.com", "password123", false);
        when(userService.getUserByEmail("test@example.com", "password123")).thenReturn(testUser);

        // When
        Session session = sessionService.sessionLoginRequest(loginRequest);

        // Then
        assertThat(session).isNotNull();
        assertThat(session.accessToken()).isEqualTo("access-token");
        assertThat(session.refreshToken()).isEqualTo("refresh-token");
        assertThat(session.user().username()).isEqualTo("testuser");

        verify(userService, times(1)).getUserByEmail("test@example.com", "password123");
        verify(redisService, times(1)).save(eq("refresh:testuser"), eq("refresh-token"));
        verify(redisService, times(1)).expiry(eq("refresh:testuser"), eq(86400L), eq(TimeUnit.SECONDS));
    }

    @Test
    @DisplayName("sessionLoginRequest - Doit rejeter un email invalide")
    void sessionLoginRequest_shouldRejectInvalidEmail() {
        // Given
        UserConnection loginRequest = new UserConnection("invalid@email", "password123", false);

        // When & Then
        assertThatThrownBy(() -> sessionService.sessionLoginRequest(loginRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email invalide");

        verify(userService, never()).getUserByEmail(anyString(), anyString());
        verify(userService, never()).getUserByUsernameAndPassword(anyString(), anyString());
    }

    @Test
    @DisplayName("sessionLoginRequest - Doit connecter l'utilisateur avec username")
    void sessionLoginRequest_shouldLoginWithUsername() {
        // Given
        setupTokenMocks();
        UserConnection loginRequest = new UserConnection("testuser", "password123", false);
        when(userService.getUserByUsernameAndPassword("testuser", "password123")).thenReturn(testUser);

        // When
        Session session = sessionService.sessionLoginRequest(loginRequest);

        // Then
        assertThat(session).isNotNull();
        assertThat(session.accessToken()).isEqualTo("access-token");
        assertThat(session.refreshToken()).isEqualTo("refresh-token");

        verify(userService, times(1)).getUserByUsernameAndPassword("testuser", "password123");
        verify(redisService, times(1)).save(eq("refresh:testuser"), eq("refresh-token"));
    }

    @Test
    @DisplayName("checkRefreshToken - Doit valider un refresh token correct")
    void checkRefreshToken_shouldValidateCorrectToken() {
        // Given
        String refreshToken = "valid-refresh-token";
        String username = "testuser";
        String storedToken = "valid-refresh-token";

        when(tokenService.extractUsername(refreshToken)).thenReturn(username);
        when(redisService.get("refresh:testuser")).thenReturn(storedToken);
        when(tokenService.isEquals(refreshToken, storedToken)).thenReturn(true);

        // When
        boolean isValid = sessionService.checkRefreshToken(refreshToken);

        // Then
        assertThat(isValid).isTrue();
        verify(tokenService, times(1)).extractUsername(refreshToken);
        verify(redisService, times(1)).get("refresh:testuser");
        verify(tokenService, times(1)).isEquals(refreshToken, storedToken);
    }

    @Test
    @DisplayName("checkRefreshToken - Doit rejeter un refresh token incorrect")
    void checkRefreshToken_shouldRejectIncorrectToken() {
        // Given
        String refreshToken = "invalid-refresh-token";
        String username = "testuser";
        String storedToken = "valid-refresh-token";

        when(tokenService.extractUsername(refreshToken)).thenReturn(username);
        when(redisService.get("refresh:testuser")).thenReturn(storedToken);
        when(tokenService.isEquals(refreshToken, storedToken)).thenReturn(false);

        // When
        boolean isValid = sessionService.checkRefreshToken(refreshToken);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("refreshToken - Doit générer de nouveaux tokens")
    void refreshToken_shouldGenerateNewTokens() {
        // Given
        setupTokenMocks();
        String refreshToken = "old-refresh-token";
        String username = "testuser";

        when(tokenService.extractUsername(refreshToken)).thenReturn(username);
        when(userService.getUserByUsername(username)).thenReturn(testUser);

        // When
        Map<String, String> tokens = sessionService.refreshToken(refreshToken);

        // Then
        assertThat(tokens).containsKeys("access_token", "refresh_token");
        assertThat(tokens.get("access_token")).isEqualTo("access-token");
        assertThat(tokens.get("refresh_token")).isEqualTo("refresh-token");

        verify(tokenService, times(1)).extractUsername(refreshToken);
        verify(userService, times(1)).getUserByUsername(username);
        verify(redisService, times(1)).save(eq("refresh:testuser"), eq("refresh-token"));
    }

    @Test
    @DisplayName("verifyEmailWithCode - Doit vérifier l'email et activer l'utilisateur")
    void verifyEmailWithCode_shouldVerifyEmailAndEnableUser() {
        // Given
        String code = "123456";
        String email = "test@example.com";

        when(emailVerificationService.verifyAndConsumeCode(email, code)).thenReturn(Optional.of(email));
        when(userService.isUserEnabled(email)).thenReturn(false);

        // When
        sessionService.verifyEmailWithCode(email, code);

        // Then
        verify(emailVerificationService, times(1)).verifyAndConsumeCode(email, code);
        verify(userService, times(1)).isUserEnabled(email);
        verify(userService, times(1)).enableUser(email);
    }

    @Test
    @DisplayName("verifyEmailWithCode - Doit rejeter un code invalide")
    void verifyEmailWithCode_shouldRejectInvalidCode() {
        // Given
        String code = "wrong-code";
        String email = "test@example.com";

        when(emailVerificationService.verifyAndConsumeCode(email, code)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> sessionService.verifyEmailWithCode(email, code))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessage("Code de vérification invalide, expiré ou nombre maximum de tentatives dépassé");

        verify(emailVerificationService, times(1)).verifyAndConsumeCode(email, code);
        verify(userService, never()).enableUser(anyString());
    }

    @Test
    @DisplayName("verifyEmailWithCode - Doit rejeter si l'utilisateur est déjà vérifié")
    void verifyEmailWithCode_shouldRejectIfUserAlreadyVerified() {
        // Given
        String code = "123456";
        String email = "test@example.com";

        when(emailVerificationService.verifyAndConsumeCode(email, code)).thenReturn(Optional.of(email));
        when(userService.isUserEnabled(email)).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> sessionService.verifyEmailWithCode(email, code))
                .isInstanceOf(UserAlreadyVerifiedException.class)
                .hasMessage("Votre compte est déjà vérifié. Vous pouvez vous connecter normalement.");

        verify(emailVerificationService, times(1)).verifyAndConsumeCode(email, code);
        verify(userService, times(1)).isUserEnabled(email);
        verify(userService, never()).enableUser(anyString());
    }

    @Test
    @DisplayName("resendVerificationEmail - Doit renvoyer l'email de vérification")
    void resendVerificationEmail_shouldResendVerificationEmail() {
        // Given
        String email = "test@example.com";

        when(userService.findByEmail(email)).thenReturn(testUser);
        when(userService.isUserEnabled(email)).thenReturn(false);

        // When
        sessionService.resendVerificationEmail(email);

        // Then
        verify(userService, times(1)).findByEmail(email);
        verify(userService, times(1)).isUserEnabled(email);
        verify(emailVerificationService, times(1)).sendVerificationEmail(email, frontendUrl);
    }

    @Test
    @DisplayName("resendVerificationEmail - Doit rejeter si l'utilisateur est déjà vérifié")
    void resendVerificationEmail_shouldRejectIfUserAlreadyVerified() {
        // Given
        String email = "test@example.com";

        when(userService.findByEmail(email)).thenReturn(testUser);
        when(userService.isUserEnabled(email)).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> sessionService.resendVerificationEmail(email))
                .isInstanceOf(UserAlreadyVerifiedException.class)
                .hasMessage("Votre compte est déjà vérifié. Vous pouvez vous connecter normalement.");

        verify(emailVerificationService, never()).sendVerificationEmail(anyString(), anyString());
    }

    @Test
    @DisplayName("invalidateRefreshToken - Doit invalider le refresh token")
    void invalidateRefreshToken_shouldInvalidateRefreshToken() {
        // Given
        String refreshToken = "refresh-token";
        String username = "testuser";

        when(tokenService.extractUsername(refreshToken)).thenReturn(username);

        // When
        sessionService.invalidateRefreshToken(refreshToken);

        // Then
        verify(tokenService, times(1)).extractUsername(refreshToken);
        verify(redisService, times(1)).delete("refresh:testuser");
    }
}