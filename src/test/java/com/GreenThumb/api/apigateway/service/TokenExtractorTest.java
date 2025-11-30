package com.GreenThumb.api.apigateway.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("TokenExtractor - Tests unitaires")
class TokenExtractorTest {

    private TokenExtractor tokenExtractor;

    @BeforeEach
    void setUp() {
        tokenExtractor = new TokenExtractor();
    }

    @Test
    @DisplayName("extractToken - Doit extraire le token d'un header Authorization valide")
    void extractToken_shouldExtractTokenFromValidAuthorizationHeader() {
        // Given
        String authorizationHeader = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test.token";

        // When
        String token = tokenExtractor.extractToken(authorizationHeader);

        // Then
        assertThat(token).isEqualTo("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test.token");
    }

    @Test
    @DisplayName("extractToken - Doit extraire le token avec espaces supplémentaires")
    void extractToken_shouldExtractTokenWithExtraSpaces() {
        // Given
        String authorizationHeader = "Bearer   eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test.token  ";

        // When
        String token = tokenExtractor.extractToken(authorizationHeader);

        // Then
        assertThat(token).isEqualTo("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test.token");
    }

    @Test
    @DisplayName("extractToken - Doit lever une exception si le header est null")
    void extractToken_shouldThrowExceptionIfHeaderIsNull() {
        // When & Then
        assertThatThrownBy(() -> tokenExtractor.extractToken(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Authorization header is required");
    }

    @Test
    @DisplayName("extractToken - Doit lever une exception si le header est vide")
    void extractToken_shouldThrowExceptionIfHeaderIsEmpty() {
        // When & Then
        assertThatThrownBy(() -> tokenExtractor.extractToken(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Authorization header is required");
    }

    @Test
    @DisplayName("extractToken - Doit lever une exception si le header contient uniquement des espaces")
    void extractToken_shouldThrowExceptionIfHeaderIsOnlySpaces() {
        // When & Then
        assertThatThrownBy(() -> tokenExtractor.extractToken("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Authorization header is required");
    }

    @Test
    @DisplayName("extractToken - Doit lever une exception si le format ne commence pas par 'Bearer '")
    void extractToken_shouldThrowExceptionIfFormatInvalid() {
        // When & Then
        assertThatThrownBy(() -> tokenExtractor.extractToken("Basic abc123"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid token format");
    }

    @Test
    @DisplayName("extractToken - Doit lever une exception si 'Bearer' est en minuscules")
    void extractToken_shouldThrowExceptionIfBearerIsLowercase() {
        // When & Then
        assertThatThrownBy(() -> tokenExtractor.extractToken("bearer token123"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid token format");
    }

    @Test
    @DisplayName("extractToken - Doit lever une exception si le header est trop court")
    void extractToken_shouldThrowExceptionIfHeaderIsTooShort() {
        // When & Then
        assertThatThrownBy(() -> tokenExtractor.extractToken("Bear"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid token format");
    }

    @Test
    @DisplayName("extractToken - Doit lever une exception si le token est vide après 'Bearer '")
    void extractToken_shouldThrowExceptionIfTokenIsEmpty() {
        // When & Then
        assertThatThrownBy(() -> tokenExtractor.extractToken("Bearer "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Token cannot be empty");
    }

    @Test
    @DisplayName("extractToken - Doit lever une exception si le token contient uniquement des espaces après 'Bearer '")
    void extractToken_shouldThrowExceptionIfTokenIsOnlySpaces() {
        // When & Then
        assertThatThrownBy(() -> tokenExtractor.extractToken("Bearer    "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Token cannot be empty");
    }

    @Test
    @DisplayName("extractToken - Doit gérer un header avec 'Bearer' sans espace")
    void extractToken_shouldHandleHeaderWithBearerNoSpace() {
        // Given
        String authorizationHeader = "Bearertoken123";

        // When & Then
        assertThatThrownBy(() -> tokenExtractor.extractToken(authorizationHeader))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid token format");
    }

    @Test
    @DisplayName("extractToken - Doit extraire un token JWT complet")
    void extractToken_shouldExtractCompleteJwtToken() {
        // Given
        String jwtToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
        String authorizationHeader = "Bearer " + jwtToken;

        // When
        String token = tokenExtractor.extractToken(authorizationHeader);

        // Then
        assertThat(token).isEqualTo(jwtToken);
    }

    @Test
    @DisplayName("extractToken - Doit gérer un token court")
    void extractToken_shouldHandleShortToken() {
        // Given
        String authorizationHeader = "Bearer abc";

        // When
        String token = tokenExtractor.extractToken(authorizationHeader);

        // Then
        assertThat(token).isEqualTo("abc");
    }

    @Test
    @DisplayName("extractToken - Doit gérer un token avec caractères spéciaux")
    void extractToken_shouldHandleTokenWithSpecialCharacters() {
        // Given
        String authorizationHeader = "Bearer token-with_special.characters123";

        // When
        String token = tokenExtractor.extractToken(authorizationHeader);

        // Then
        assertThat(token).isEqualTo("token-with_special.characters123");
    }

    @Test
    @DisplayName("extractToken - Doit gérer un header avec plusieurs espaces après 'Bearer'")
    void extractToken_shouldHandleMultipleSpacesAfterBearer() {
        // Given
        String authorizationHeader = "Bearer     token123";

        // When
        String token = tokenExtractor.extractToken(authorizationHeader);

        // Then
        assertThat(token).isEqualTo("token123");
    }
}
