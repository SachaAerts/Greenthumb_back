package com.GreenThumb.api.apigateway.service;

import com.GreenThumb.api.infrastructure.service.TokenService;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("TokenService - Tests unitaires")
class TokenServiceTest {

    private TokenService tokenService;
    private String secret;
    private long accessTokenExpirationMs;
    private long refreshTokenExpirationMs;

    @BeforeEach
    void setUp() {
        secret = "test-secret-key-must-be-at-least-256-bits-long-for-hs256-algorithm";
        accessTokenExpirationMs = 3600000; // 1 heure
        refreshTokenExpirationMs = 604800000; // 7 jours

        tokenService = new TokenService(secret, accessTokenExpirationMs, refreshTokenExpirationMs);
    }

    @Test
    @DisplayName("generateAccessToken - Doit générer un access token valide avec claims")
    void generateAccessToken_shouldGenerateValidTokenWithClaims() {
        // Given
        String username = "testuser";
        Map<String, Object> claims = Map.of("role", "USER", "verified", true);

        // When
        String token = tokenService.generateAccessToken(username, claims);

        // Then
        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
        assertThat(tokenService.isTokenValid(token)).isTrue();
        assertThat(tokenService.extractUsername(token)).isEqualTo(username);

        Map<String, Object> extractedClaims = tokenService.extractClaims(token);
        assertThat(extractedClaims.get("role")).isEqualTo("USER");
        assertThat(extractedClaims.get("verified")).isEqualTo(true);
    }

    @Test
    @DisplayName("generateRefreshToken - Doit générer un refresh token valide sans claims")
    void generateRefreshToken_shouldGenerateValidTokenWithoutClaims() {
        // Given
        String username = "testuser";

        // When
        String token = tokenService.generateRefreshToken(username);

        // Then
        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
        assertThat(tokenService.isTokenValid(token)).isTrue();
        assertThat(tokenService.extractUsername(token)).isEqualTo(username);
    }

    @Test
    @DisplayName("isTokenValid - Doit valider un token correct")
    void isTokenValid_shouldValidateCorrectToken() {
        // Given
        String username = "testuser";
        String token = tokenService.generateAccessToken(username, Map.of());

        // When
        boolean isValid = tokenService.isTokenValid(token);

        // Then
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("isTokenValid - Doit rejeter un token invalide")
    void isTokenValid_shouldRejectInvalidToken() {
        // Given
        String invalidToken = "invalid.token.here";

        // When
        boolean isValid = tokenService.isTokenValid(invalidToken);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("isTokenValid - Doit rejeter un token null")
    void isTokenValid_shouldRejectNullToken() {
        // When
        boolean isValid = tokenService.isTokenValid(null);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("isTokenValid - Doit rejeter un token vide")
    void isTokenValid_shouldRejectEmptyToken() {
        // When
        boolean isValid = tokenService.isTokenValid("");

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("extractUsername - Doit extraire le username d'un token valide")
    void extractUsername_shouldExtractUsernameFromValidToken() {
        // Given
        String username = "testuser";
        String token = tokenService.generateAccessToken(username, Map.of());

        // When
        String extractedUsername = tokenService.extractUsername(token);

        // Then
        assertThat(extractedUsername).isEqualTo(username);
    }

    @Test
    @DisplayName("extractUsername - Doit lancer une exception pour un token invalide")
    void extractUsername_shouldThrowExceptionForInvalidToken() {
        // Given
        String invalidToken = "invalid.token.here";

        // When & Then
        assertThatThrownBy(() -> tokenService.extractUsername(invalidToken))
                .isInstanceOf(JwtException.class);
    }

    @Test
    @DisplayName("extractClaims - Doit extraire tous les claims d'un token")
    void extractClaims_shouldExtractAllClaimsFromToken() {
        // Given
        String username = "testuser";
        Map<String, Object> claims = Map.of(
                "role", "ADMIN",
                "verified", true,
                "level", 5
        );
        String token = tokenService.generateAccessToken(username, claims);

        // When
        Map<String, Object> extractedClaims = tokenService.extractClaims(token);

        // Then
        assertThat(extractedClaims).isNotNull();
        assertThat(extractedClaims.get("role")).isEqualTo("ADMIN");
        assertThat(extractedClaims.get("verified")).isEqualTo(true);
        assertThat(extractedClaims.get("level")).isEqualTo(5);
        assertThat(extractedClaims.get("sub")).isEqualTo(username);
    }

    @Test
    @DisplayName("isEquals - Doit retourner true pour des tokens identiques")
    void isEquals_shouldReturnTrueForIdenticalTokens() {
        // Given
        String token1 = "token123";
        String token2 = "token123";

        // When
        boolean isEqual = tokenService.isEquals(token1, token2);

        // Then
        assertThat(isEqual).isTrue();
    }

    @Test
    @DisplayName("isEquals - Doit retourner true pour des tokens identiques avec espaces")
    void isEquals_shouldReturnTrueForIdenticalTokensWithSpaces() {
        // Given
        String token1 = "  token123  ";
        String token2 = "token123";

        // When
        boolean isEqual = tokenService.isEquals(token1, token2);

        // Then
        assertThat(isEqual).isTrue();
    }

    @Test
    @DisplayName("isEquals - Doit retourner false pour des tokens différents")
    void isEquals_shouldReturnFalseForDifferentTokens() {
        // Given
        String token1 = "token123";
        String token2 = "token456";

        // When
        boolean isEqual = tokenService.isEquals(token1, token2);

        // Then
        assertThat(isEqual).isFalse();
    }

    @Test
    @DisplayName("isEquals - Doit retourner false si un token est null")
    void isEquals_shouldReturnFalseIfTokenIsNull() {
        // Given
        String token1 = "token123";
        String token2 = null;

        // When
        boolean isEqual = tokenService.isEquals(token1, token2);

        // Then
        assertThat(isEqual).isFalse();
    }

    @Test
    @DisplayName("isEquals - Doit retourner false si les deux tokens sont null")
    void isEquals_shouldReturnFalseIfBothTokensAreNull() {
        // When
        boolean isEqual = tokenService.isEquals(null, null);

        // Then
        assertThat(isEqual).isFalse();
    }

    @Test
    @DisplayName("generateAccessToken - Deux tokens générés doivent être différents après 1 seconde")
    void generateAccessToken_twoGeneratedTokensShouldBeDifferent() throws InterruptedException {
        // Given
        String username = "testuser";
        Map<String, Object> claims = Map.of("role", "USER");

        // When
        String token1 = tokenService.generateAccessToken(username, claims);
        Thread.sleep(2000); // Ensure different timestamp (JWT uses seconds, not milliseconds)
        String token2 = tokenService.generateAccessToken(username, claims);

        // Then
        assertThat(token1).isNotEqualTo(token2);
        assertThat(tokenService.isTokenValid(token1)).isTrue();
        assertThat(tokenService.isTokenValid(token2)).isTrue();
    }

    @Test
    @DisplayName("generateAccessToken - Doit gérer les claims vides")
    void generateAccessToken_shouldHandleEmptyClaims() {
        // Given
        String username = "testuser";
        Map<String, Object> emptyClaims = Map.of();

        // When
        String token = tokenService.generateAccessToken(username, emptyClaims);

        // Then
        assertThat(token).isNotNull();
        assertThat(tokenService.isTokenValid(token)).isTrue();
        assertThat(tokenService.extractUsername(token)).isEqualTo(username);
    }

    @Test
    @DisplayName("generateAccessToken - Doit gérer les noms d'utilisateur avec caractères spéciaux")
    void generateAccessToken_shouldHandleSpecialCharactersInUsername() {
        // Given
        String username = "test.user@domain.com";
        Map<String, Object> claims = Map.of("role", "USER");

        // When
        String token = tokenService.generateAccessToken(username, claims);

        // Then
        assertThat(token).isNotNull();
        assertThat(tokenService.isTokenValid(token)).isTrue();
        assertThat(tokenService.extractUsername(token)).isEqualTo(username);
    }
}
