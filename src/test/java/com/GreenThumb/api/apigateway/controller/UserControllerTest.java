package com.GreenThumb.api.apigateway.controller;

import com.GreenThumb.api.apigateway.service.TokenExtractor;
import com.GreenThumb.api.apigateway.service.UserServiceGateway;
import com.GreenThumb.api.apigateway.validation.PaginationValidator;
import com.GreenThumb.api.apigateway.validation.UsernameValidator;
import com.GreenThumb.api.plant.application.dto.PlantDto;
import com.GreenThumb.api.plant.application.facade.PlantFacade;
import com.GreenThumb.api.user.application.dto.UserDto;
import com.GreenThumb.api.user.domain.exception.NoFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = UserController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                        classes = {
                                com.GreenThumb.api.config.SecurityConfig.class,
                                com.GreenThumb.api.config.JwtAuthenticationFilter.class
                        })
        }
)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserServiceGateway userService;

    @MockitoBean
    private TokenExtractor tokenExtractor;

    @MockitoBean
    private PaginationValidator paginationValidator;

    @MockitoBean
    private UsernameValidator usernameValidator;

    @MockitoBean
    private PlantFacade plantFacade;

    @Test
    @DisplayName("GET /api/users/count - Doit retourner le nombre d'utilisateurs")
    void getUserCount_shouldReturnUserCount() throws Exception {
        // Given
        when(userService.countUsers()).thenReturn(42L);

        // When & Then
        mockMvc.perform(get("/api/users/count"))
                .andExpect(status().isOk())
                .andExpect(content().string("42"));
    }

    @Test
    @DisplayName("GET /api/users/count - Doit retourner 0 quand aucun utilisateur")
    void getUserCount_shouldReturnZeroWhenNoUsers() throws Exception {
        // Given
        when(userService.countUsers()).thenReturn(0L);

        // When & Then
        mockMvc.perform(get("/api/users/count"))
                .andExpect(status().isOk())
                .andExpect(content().string("0"));
    }

    @Test
    @DisplayName("GET /api/users/{username}/plants - Doit retourner les plantes d'un utilisateur avec pagination par défaut")
    void getAllPlant_shouldReturnUserPlantsWithDefaultPagination() throws Exception {
        // Given
        PlantDto plant1 = new PlantDto("slug1", "Scientific Name 1", "Common Name 1", "http://image1.jpg", Collections.emptyList());
        PlantDto plant2 = new PlantDto("slug2", "Scientific Name 2", "Common Name 2", "http://image2.jpg", Collections.emptyList());
        Page<PlantDto> plantsPage = new PageImpl<>(List.of(plant1, plant2));

        doNothing().when(usernameValidator).validate("testuser");
        doNothing().when(paginationValidator).validate(0, 5);
        when(userService.getAllPlantsByUsername(eq("testuser"), eq(0), eq(5)))
                .thenReturn(plantsPage);

        // When & Then
        mockMvc.perform(get("/api/users/testuser/plants"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2));
    }

    @Test
    @DisplayName("GET /api/users/{username}/plants - Doit retourner les plantes avec pagination personnalisée")
    void getAllPlant_shouldReturnUserPlantsWithCustomPagination() throws Exception {
        // Given
        PlantDto plant1 = new PlantDto("slug1", "Scientific Name 1", "Common Name 1", "http://image1.jpg", Collections.emptyList());
        Page<PlantDto> plantsPage = new PageImpl<>(List.of(plant1));

        doNothing().when(usernameValidator).validate("testuser");
        doNothing().when(paginationValidator).validate(1, 10);
        when(userService.getAllPlantsByUsername(eq("testuser"), eq(1), eq(10)))
                .thenReturn(plantsPage);

        // When & Then
        mockMvc.perform(get("/api/users/testuser/plants")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1));
    }

    @Test
    @DisplayName("GET /api/users/{username}/plants - Doit retourner une page vide quand aucune plante")
    void getAllPlant_shouldReturnEmptyPageWhenNoPlants() throws Exception {
        // Given
        Page<PlantDto> emptyPage = new PageImpl<>(Collections.emptyList());

        doNothing().when(usernameValidator).validate("testuser");
        doNothing().when(paginationValidator).validate(0, 5);
        when(userService.getAllPlantsByUsername(eq("testuser"), anyInt(), anyInt()))
                .thenReturn(emptyPage);

        // When & Then
        mockMvc.perform(get("/api/users/testuser/plants"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(0));
    }

    @Test
    @DisplayName("GET /api/users/{username}/plants - Doit gérer l'exception JsonProcessingException")
    void getAllPlant_shouldHandleJsonProcessingException() throws Exception {
        // Given
        doNothing().when(usernameValidator).validate("testuser");
        doNothing().when(paginationValidator).validate(0, 5);
        when(userService.getAllPlantsByUsername(eq("testuser"), anyInt(), anyInt()))
                .thenThrow(new JsonProcessingException("JSON parsing error") {});

        // When & Then
        mockMvc.perform(get("/api/users/testuser/plants"))
                .andExpect(status().is5xxServerError());
    }

    @Test
    @DisplayName("GET /api/users/{username}/plants - Doit rejeter un numéro de page négatif (erreur 400)")
    void getAllPlant_shouldRejectNegativePageNumber() throws Exception {
        // Given
        doNothing().when(usernameValidator).validate("testuser");
        doThrow(new IllegalArgumentException("Page number must be >= 0"))
                .when(paginationValidator).validate(-1, 5);

        // When & Then
        mockMvc.perform(get("/api/users/testuser/plants")
                        .param("page", "-1")
                        .param("size", "5"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Page number must be >= 0"));
    }

    @Test
    @DisplayName("GET /api/users/{username}/plants - Doit rejeter une taille de page nulle (erreur 400)")
    void getAllPlant_shouldRejectZeroPageSize() throws Exception {
        // Given
        doNothing().when(usernameValidator).validate("testuser");
        doThrow(new IllegalArgumentException("Page size must be between 1 and 100"))
                .when(paginationValidator).validate(0, 0);

        // When & Then
        mockMvc.perform(get("/api/users/testuser/plants")
                        .param("page", "0")
                        .param("size", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Page size must be between 1 and 100"));
    }

    @Test
    @DisplayName("GET /api/users/{username}/plants - Doit rejeter une taille de page négative (erreur 400)")
    void getAllPlant_shouldRejectNegativePageSize() throws Exception {
        // Given
        doNothing().when(usernameValidator).validate("testuser");
        doThrow(new IllegalArgumentException("Page size must be between 1 and 100"))
                .when(paginationValidator).validate(0, -5);

        // When & Then
        mockMvc.perform(get("/api/users/testuser/plants")
                        .param("page", "0")
                        .param("size", "-5"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Page size must be between 1 and 100"));
    }

    @Test
    @DisplayName("GET /api/users/{username}/plants - Doit rejeter une taille de page trop grande (erreur 400)")
    void getAllPlant_shouldRejectPageSizeTooLarge() throws Exception {
        // Given
        doNothing().when(usernameValidator).validate("testuser");
        doThrow(new IllegalArgumentException("Page size must be between 1 and 100"))
                .when(paginationValidator).validate(0, 101);

        // When & Then
        mockMvc.perform(get("/api/users/testuser/plants")
                        .param("page", "0")
                        .param("size", "101"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Page size must be between 1 and 100"));
    }

    @Test
    @DisplayName("GET /api/users/{username}/plants - Doit accepter la taille maximale de page (100)")
    void getAllPlant_shouldAcceptMaxPageSize() throws Exception {
        // Given
        Page<PlantDto> plantsPage = new PageImpl<>(Collections.emptyList());
        doNothing().when(usernameValidator).validate("testuser");
        doNothing().when(paginationValidator).validate(0, 100);
        when(userService.getAllPlantsByUsername(eq("testuser"), eq(0), eq(100)))
                .thenReturn(plantsPage);

        // When & Then
        mockMvc.perform(get("/api/users/testuser/plants")
                        .param("page", "0")
                        .param("size", "100"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/users/me - Doit retourner les informations de l'utilisateur connecté")
    void getMe_shouldReturnCurrentUserInfo() throws Exception {
        // Given
        UserDto userDto = new UserDto(
                "testuser",
                "Test",
                "User",
                "test@example.com",
                "0123456789",
                "My bio",
                false,
                "USER",
                "default-avatar.png"
        );

        when(tokenExtractor.extractToken("Bearer valid-token-123")).thenReturn("valid-token-123");
        when(userService.getMe("valid-token-123")).thenReturn(userDto);

        // When & Then
        mockMvc.perform(get("/api/users/me")
                        .header("Authorization", "Bearer valid-token-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.firstname").value("Test"))
                .andExpect(jsonPath("$.lastname").value("User"));
    }

    @Test
    @DisplayName("GET /api/users/me - Doit gérer l'absence d'en-tête Authorization (erreur 400)")
    void getMe_shouldHandleMissingAuthorizationHeader() throws Exception {
        // Given
        when(tokenExtractor.extractToken(null))
                .thenThrow(new IllegalArgumentException("Authorization header is required"));

        // When & Then
        // IllegalArgumentException lancée par validation, gérée par GlobalExceptionHandler
        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Authorization header is required"));
    }

    @Test
    @DisplayName("GET /api/users/me - Doit gérer un en-tête Authorization vide (erreur 400)")
    void getMe_shouldHandleEmptyAuthorizationHeader() throws Exception {
        // Given
        when(tokenExtractor.extractToken(""))
                .thenThrow(new IllegalArgumentException("Authorization header is required"));

        // When & Then
        mockMvc.perform(get("/api/users/me")
                        .header("Authorization", ""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Authorization header is required"));
    }

    @Test
    @DisplayName("GET /api/users/me - Doit gérer un en-tête sans préfixe Bearer (erreur 400)")
    void getMe_shouldHandleAuthorizationHeaderWithoutBearer() throws Exception {
        // Given
        when(tokenExtractor.extractToken("InvalidToken123"))
                .thenThrow(new IllegalArgumentException("Invalid token format"));

        // When & Then
        mockMvc.perform(get("/api/users/me")
                        .header("Authorization", "InvalidToken123"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid token format"));
    }

    @Test
    @DisplayName("GET /api/users/me - Doit gérer un en-tête Authorization trop court (erreur 400)")
    void getMe_shouldHandleTooShortAuthorizationHeader() throws Exception {
        // Given
        when(tokenExtractor.extractToken("Bearer"))
                .thenThrow(new IllegalArgumentException("Invalid token format"));

        // When & Then
        mockMvc.perform(get("/api/users/me")
                        .header("Authorization", "Bearer"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid token format"));
    }

    @Test
    @DisplayName("GET /api/users/me - Doit gérer un token vide après Bearer (erreur 400)")
    void getMe_shouldHandleEmptyTokenAfterBearer() throws Exception {
        // Given
        when(tokenExtractor.extractToken("Bearer "))
                .thenThrow(new IllegalArgumentException("Token cannot be empty"));

        // When & Then
        mockMvc.perform(get("/api/users/me")
                        .header("Authorization", "Bearer "))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Token cannot be empty"));
    }

    @Test
    @DisplayName("GET /api/users/me - Doit gérer un token invalide avec exception du service (erreur 500)")
    void getMe_shouldHandleInvalidToken() throws Exception {
        // Given
        when(tokenExtractor.extractToken("Bearer invalid-token")).thenReturn("invalid-token");
        when(userService.getMe("invalid-token"))
                .thenThrow(new RuntimeException("Invalid token"));

        // When & Then
        // GlobalExceptionHandler retourne 500 INTERNAL_SERVER_ERROR pour les Exception génériques
        mockMvc.perform(get("/api/users/me")
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Invalid token"));
    }

    @Test
    @DisplayName("GET /api/users/me - Doit gérer l'exception JsonProcessingException")
    void getMe_shouldHandleJsonProcessingException() throws Exception {
        // Given
        when(tokenExtractor.extractToken("Bearer valid-token")).thenReturn("valid-token");
        when(userService.getMe("valid-token"))
                .thenThrow(new JsonProcessingException("JSON parsing error") {});

        // When & Then
        mockMvc.perform(get("/api/users/me")
                        .header("Authorization", "Bearer valid-token"))
                .andExpect(status().is5xxServerError());
    }

    @Test
    @DisplayName("GET /api/users/me - Doit retourner un utilisateur avec le rôle ADMIN")
    void getMe_shouldReturnAdminUser() throws Exception {
        // Given
        UserDto adminDto = new UserDto(
                "adminuser",
                "Admin",
                "User",
                "admin@example.com",
                "0123456789",
                "Admin bio",
                false,
                "ADMIN",
                "admin-avatar.png"
        );

        when(tokenExtractor.extractToken("Bearer admin-token")).thenReturn("admin-token");
        when(userService.getMe("admin-token")).thenReturn(adminDto);

        // When & Then
        mockMvc.perform(get("/api/users/me")
                        .header("Authorization", "Bearer admin-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("adminuser"))
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @Test
    @DisplayName("GET /api/users/me - Doit retourner un compte privé")
    void getMe_shouldReturnPrivateUser() throws Exception {
        // Given
        UserDto privateUserDto = new UserDto(
                "privateuser",
                "Private",
                "User",
                "private@example.com",
                "0123456789",
                "Bio",
                true,
                "USER",
                "private-avatar.png"
        );

        when(tokenExtractor.extractToken("Bearer token")).thenReturn("token");
        when(userService.getMe("token")).thenReturn(privateUserDto);

        // When & Then
        mockMvc.perform(get("/api/users/me")
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isPrivate").value(true));
    }

    @Test
    @DisplayName("GET /api/users/{username}/tasks - Doit retourner le nombre de tâches de l'utilisateur")
    void getUserTasks_shouldReturnTaskCount() throws Exception {
        // Given
        String username = "testuser";
        long userId = 123L;
        long taskCount = 5L;

        when(userService.getIdByUsername(username)).thenReturn(userId);
        when(plantFacade.countTask(userId)).thenReturn(taskCount);

        // When & Then
        mockMvc.perform(get("/api/users/{username}/tasks", username))
                .andExpect(status().isOk())
                .andExpect(content().string("5"));
    }

    @Test
    @DisplayName("GET /api/users/{username}/tasks - Doit retourner 0 quand l'utilisateur n'a pas de tâches")
    void getUserTasks_shouldReturnZeroWhenNoTasks() throws Exception {
        // Given
        String username = "testuser";
        long userId = 123L;

        when(userService.getIdByUsername(username)).thenReturn(userId);
        when(plantFacade.countTask(userId)).thenReturn(0L);

        // When & Then
        mockMvc.perform(get("/api/users/{username}/tasks", username))
                .andExpect(status().isOk())
                .andExpect(content().string("0"));
    }

    @Test
    @DisplayName("GET /api/users/{username}/tasks - Doit retourner un grand nombre de tâches")
    void getUserTasks_shouldReturnLargeTaskCount() throws Exception {
        // Given
        String username = "testuser";
        long userId = 123L;
        long taskCount = 1000L;

        when(userService.getIdByUsername(username)).thenReturn(userId);
        when(plantFacade.countTask(userId)).thenReturn(taskCount);

        // When & Then
        mockMvc.perform(get("/api/users/{username}/tasks", username))
                .andExpect(status().isOk())
                .andExpect(content().string("1000"));
    }

    @Test
    @DisplayName("GET /api/users/{username}/tasks - Doit gérer un username contenant uniquement des espaces")
    void getUserTasks_shouldHandleWhitespaceUsername() throws Exception {
        // Given
        String username = "   ";

        when(userService.getIdByUsername(username))
                .thenThrow(new NoFoundException("Utilisateur non trouvé"));

        // When & Then
        mockMvc.perform(get("/api/users/{username}/tasks", username))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Utilisateur non trouvé"));
    }

    @Test
    @DisplayName("GET /api/users/{username}/tasks - Doit gérer l'utilisateur inexistant (erreur 404)")
    void getUserTasks_shouldHandleUserNotFound() throws Exception {
        // Given
        String username = "nonexistentuser";

        when(userService.getIdByUsername(username))
                .thenThrow(new NoFoundException("Utilisateur non trouvé"));

        // When & Then
        mockMvc.perform(get("/api/users/{username}/tasks", username))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Utilisateur non trouvé"));
    }
}
