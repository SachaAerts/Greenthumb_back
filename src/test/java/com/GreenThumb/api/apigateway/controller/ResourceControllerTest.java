package com.GreenThumb.api.apigateway.controller;

import com.GreenThumb.api.apigateway.dto.Resource;
import com.GreenThumb.api.apigateway.service.ResourceServiceApi;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = ResourceController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                        classes = {
                                com.GreenThumb.api.config.SecurityConfig.class,
                                com.GreenThumb.api.config.JwtAuthenticationFilter.class
                        })
        }
)
@AutoConfigureMockMvc(addFilters = false)
class ResourceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ResourceServiceApi resourceService;

    @Test
    @DisplayName("GET /api/resources/three-resources - Doit retourner trois ressources")
    void getThreeResource_shouldReturnThreeResources() throws Exception {
        // Given
        Resource resource1 = new Resource("Resource 1", "2024-01-01", "http://example.com/image1.jpg");
        Resource resource2 = new Resource("Resource 2", "2024-01-02", "http://example.com/image2.jpg");
        Resource resource3 = new Resource("Resource 3", "2024-01-03", "http://example.com/image3.jpg");
        List<Resource> resources = Arrays.asList(resource1, resource2, resource3);

        when(resourceService.getThreeResource()).thenReturn(resources);

        // When & Then
        mockMvc.perform(get("/api/resources/three-resources"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3));
    }

    @Test
    @DisplayName("GET /api/resources/three-resources - Doit retourner une liste vide quand aucune ressource")
    void getThreeResource_shouldReturnEmptyListWhenNoResources() throws Exception {
        // Given
        List<Resource> emptyList = Collections.emptyList();
        when(resourceService.getThreeResource()).thenReturn(emptyList);

        // When & Then
        mockMvc.perform(get("/api/resources/three-resources"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("GET /api/resources/three-resources - Doit retourner 1 ou 2 ressources si moins de 3 disponibles")
    void getThreeResource_shouldReturnLessThanThreeResourcesWhenAvailable() throws Exception {
        // Given
        Resource resource1 = new Resource("Resource 1", "2024-01-01", "http://example.com/image1.jpg");
        Resource resource2 = new Resource("Resource 2", "2024-01-02", "http://example.com/image2.jpg");
        List<Resource> resources = Arrays.asList(resource1, resource2);

        when(resourceService.getThreeResource()).thenReturn(resources);

        // When & Then
        mockMvc.perform(get("/api/resources/three-resources"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("GET /api/resources/three-resources - Doit gérer les erreurs du service avec erreur 500")
    void getThreeResource_shouldHandleServiceException() throws Exception {
        // Given
        when(resourceService.getThreeResource())
                .thenThrow(new RuntimeException("Database error"));

        // When & Then
        // GlobalExceptionHandler retourne 500 INTERNAL_SERVER_ERROR pour les Exception génériques
        mockMvc.perform(get("/api/resources/three-resources"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Database error"));
    }

    @Test
    @DisplayName("GET /api/resources/three-resources - Doit gérer une erreur de connexion externe avec erreur 500")
    void getThreeResource_shouldHandleConnectionException() throws Exception {
        // Given
        when(resourceService.getThreeResource())
                .thenThrow(new RuntimeException("External API connection failed"));

        // When & Then
        // GlobalExceptionHandler retourne 500 INTERNAL_SERVER_ERROR pour les Exception génériques
        mockMvc.perform(get("/api/resources/three-resources"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("External API connection failed"));
    }

    @Test
    @DisplayName("GET /api/resources/three-resources - Doit retourner une seule ressource quand disponible")
    void getThreeResource_shouldReturnOneResourceWhenOnlyOneAvailable() throws Exception {
        // Given
        Resource resource1 = new Resource("Resource 1", "2024-01-01", "http://example.com/image1.jpg");
        List<Resource> resources = Collections.singletonList(resource1);

        when(resourceService.getThreeResource()).thenReturn(resources);

        // When & Then
        mockMvc.perform(get("/api/resources/three-resources"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @DisplayName("GET /api/resources/three-resources - Doit gérer un timeout du service avec erreur 500")
    void getThreeResource_shouldHandleServiceTimeout() throws Exception {
        // Given
        when(resourceService.getThreeResource())
                .thenThrow(new RuntimeException("Service timeout"));

        // When & Then
        // GlobalExceptionHandler retourne 500 INTERNAL_SERVER_ERROR pour les Exception génériques
        mockMvc.perform(get("/api/resources/three-resources"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Service timeout"));
    }
}
