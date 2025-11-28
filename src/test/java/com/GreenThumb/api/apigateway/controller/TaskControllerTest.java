package com.GreenThumb.api.apigateway.controller;

import com.GreenThumb.api.plant.application.facade.PlantFacade;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = TaskController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                        classes = {
                                com.GreenThumb.api.config.SecurityConfig.class,
                                com.GreenThumb.api.config.JwtAuthenticationFilter.class
                        })
        }
)
@AutoConfigureMockMvc(addFilters = false)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PlantFacade plantFacade;

    @Test
    @DisplayName("GET /api/tasks - Doit retourner le nombre de tâches")
    void getCountTask_shouldReturnTaskCount() throws Exception {
        // Given
        when(plantFacade.countTask()).thenReturn(42L);

        // When & Then
        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(content().string("42"));
    }

    @Test
    @DisplayName("GET /api/tasks - Doit retourner 0 quand aucune tâche")
    void getCountTask_shouldReturnZeroWhenNoTasks() throws Exception {
        // Given
        when(plantFacade.countTask()).thenReturn(0L);

        // When & Then
        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(content().string("0"));
    }

    @Test
    @DisplayName("GET /api/tasks - Doit retourner un grand nombre de tâches")
    void getCountTask_shouldReturnLargeNumberOfTasks() throws Exception {
        // Given
        when(plantFacade.countTask()).thenReturn(1000L);

        // When & Then
        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(content().string("1000"));
    }

    @Test
    @DisplayName("GET /api/tasks - Doit gérer les erreurs du service avec erreur 500")
    void getCountTask_shouldHandleServiceException() throws Exception {
        // Given
        when(plantFacade.countTask())
                .thenThrow(new RuntimeException("Database error"));

        // When & Then
        // GlobalExceptionHandler retourne 500 INTERNAL_SERVER_ERROR pour les Exception génériques
        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Database error"));
    }

    @Test
    @DisplayName("GET /api/tasks - Doit gérer une erreur de connexion à la base de données avec erreur 500")
    void getCountTask_shouldHandleDatabaseConnectionException() throws Exception {
        // Given
        when(plantFacade.countTask())
                .thenThrow(new RuntimeException("Unable to connect to database"));

        // When & Then
        // GlobalExceptionHandler retourne 500 INTERNAL_SERVER_ERROR pour les Exception génériques
        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Unable to connect to database"));
    }

    @Test
    @DisplayName("GET /api/tasks - Doit gérer une timeout du service avec erreur 500")
    void getCountTask_shouldHandleServiceTimeout() throws Exception {
        // Given
        when(plantFacade.countTask())
                .thenThrow(new RuntimeException("Service timeout"));

        // When & Then
        // GlobalExceptionHandler retourne 500 INTERNAL_SERVER_ERROR pour les Exception génériques
        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Service timeout"));
    }

    @Test
    @DisplayName("GET /api/tasks - Doit retourner un nombre négatif si le service retourne une valeur négative")
    void getCountTask_shouldReturnNegativeIfServiceReturnsNegative() throws Exception {
        // Given
        when(plantFacade.countTask()).thenReturn(-1L);

        // When & Then
        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(content().string("-1"));
    }

    @Test
    @DisplayName("GET /api/tasks - Doit gérer une NullPointerException du service avec erreur 500")
    void getCountTask_shouldHandleNullPointerException() throws Exception {
        // Given
        when(plantFacade.countTask())
                .thenThrow(new NullPointerException("PlantFacade returned null"));

        // When & Then
        // GlobalExceptionHandler retourne 500 INTERNAL_SERVER_ERROR pour les Exception génériques
        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("PlantFacade returned null"));
    }
}
