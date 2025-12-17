package com.GreenThumb.api.apigateway.service;

import com.GreenThumb.api.plant.application.dto.PlantDto;
import com.GreenThumb.api.plant.application.facade.PlantFacade;
import com.GreenThumb.api.user.application.dto.UserDto;
import com.GreenThumb.api.user.application.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserServiceGateway - Tests unitaires")
class UserServiceGatewayTest {

    @Mock
    private PlantFacade plantModule;

    @Mock
    private RedisService redisService;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private UserService userService;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private UserServiceGateway userServiceGateway;

    private UserDto testUser;
    private PlantDto testPlant;

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

        testPlant = new PlantDto(
                "rose-slug",
                "Rosa",
                "Rose",
                "image.jpg",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                0,
                0,
                null,
                null,
                null,
                null,
                null,
                List.of()
        );
    }

    @Test
    @DisplayName("countUsers - Doit retourner le nombre total d'utilisateurs")
    void countUsers_shouldReturnTotalUserCount() {
        // Given
        when(userService.countUsers()).thenReturn(42L);

        // When
        long count = userServiceGateway.countUsers();

        // Then
        assertThat(count).isEqualTo(42L);
        verify(userService, times(1)).countUsers();
    }


    @Test
    @DisplayName("getMe - Doit retourner l'utilisateur depuis le cache si présent")
    void getMe_shouldReturnFromCacheWhenAvailable() throws JsonProcessingException {
        // Given
        String token = "valid-token";
        String username = "testuser";
        String userJson = "{\"username\":\"testuser\"}";

        when(tokenService.extractUsername(token)).thenReturn(username);
        when(redisService.checkKey(username)).thenReturn(true);
        when(redisService.get(username)).thenReturn(userJson);
        when(objectMapper.readValue(userJson, UserDto.class)).thenReturn(testUser);

        // When
        UserDto result = userServiceGateway.getMe(token);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.username()).isEqualTo("testuser");
        verify(tokenService, times(1)).extractUsername(token);
        verify(redisService, times(1)).checkKey(username);
        verify(redisService, times(1)).get(username);
        verify(userService, never()).getUserByUsername(anyString());
    }

    @Test
    @DisplayName("getMe - Doit récupérer depuis la DB et mettre en cache si absent")
    void getMe_shouldFetchFromDBAndCacheWhenNotInCache() throws JsonProcessingException {
        // Given
        String token = "valid-token";
        String username = "testuser";
        String userJson = "{\"username\":\"testuser\"}";

        when(tokenService.extractUsername(token)).thenReturn(username);
        when(redisService.checkKey(username)).thenReturn(false);
        when(userService.getUserByUsername(username)).thenReturn(testUser);
        when(objectMapper.writeValueAsString(testUser)).thenReturn(userJson);

        // When
        UserDto result = userServiceGateway.getMe(token);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.username()).isEqualTo("testuser");
        verify(tokenService, times(1)).extractUsername(token);
        verify(redisService, times(1)).checkKey(username);
        verify(userService, times(1)).getUserByUsername(username);
        verify(redisService, times(1)).saveJson(username, userJson);
        verify(redisService, times(1)).expiry(username, 5, TimeUnit.MINUTES);
    }

    @Test
    @DisplayName("getMe - Doit propager les exceptions JsonProcessingException")
    void getMe_shouldPropagateJsonProcessingException() throws JsonProcessingException {
        // Given
        String token = "valid-token";
        String username = "testuser";

        when(tokenService.extractUsername(token)).thenReturn(username);
        when(redisService.checkKey(username)).thenReturn(false);
        when(userService.getUserByUsername(username)).thenReturn(testUser);
        when(objectMapper.writeValueAsString(testUser)).thenThrow(new JsonProcessingException("JSON error") {});

        // When & Then
        assertThatThrownBy(() -> userServiceGateway.getMe(token))
                .isInstanceOf(JsonProcessingException.class)
                .hasMessageContaining("JSON error");
    }
}
