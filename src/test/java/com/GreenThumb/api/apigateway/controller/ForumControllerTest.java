package com.GreenThumb.api.apigateway.controller;

import com.GreenThumb.api.apigateway.dto.Message;
import com.GreenThumb.api.apigateway.service.MessageService;
import com.GreenThumb.api.apigateway.config.JwtAuthenticationFilter;
import com.GreenThumb.api.apigateway.config.SecurityConfig;
import com.GreenThumb.api.user.domain.exception.NoFoundException;
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
        controllers = ForumController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                        classes = {
                                SecurityConfig.class,
                                JwtAuthenticationFilter.class
                        })
        }
)
@AutoConfigureMockMvc(addFilters = false)
class ForumControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MessageService messageService;

    @Test
    @DisplayName("GET /api/messages/top3like - Doit retourner les 3 messages les plus likés")
    void getTop3Like_shouldReturnTop3Messages() throws Exception {
        // Given
        Message message1 = new Message(List.of("tag1", "tag2"), "Title 1", "Author 1", 100, "2024-01-01");
        Message message2 = new Message(List.of("tag3"), "Title 2", "Author 2", 50, "2024-01-02");
        Message message3 = new Message(List.of("tag4", "tag5"), "Title 3", "Author 3", 25, "2024-01-03");
        List<Message> messages = Arrays.asList(message1, message2, message3);

        when(messageService.getTop3Message()).thenReturn(messages);

        // When & Then
        mockMvc.perform(get("/api/messages/top3like"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3));
    }

    @Test
    @DisplayName("GET /api/messages/top3like - Doit retourner une liste vide quand aucun message")
    void getTop3Like_shouldReturnEmptyListWhenNoMessages() throws Exception {
        // Given
        List<Message> emptyList = Collections.emptyList();
        when(messageService.getTop3Message()).thenReturn(emptyList);

        // When & Then
        mockMvc.perform(get("/api/messages/top3like"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("GET /api/messages/top3like - Doit retourner 1 ou 2 messages si moins de 3 disponibles")
    void getTop3Like_shouldReturnLessThanThreeMessagesWhenAvailable() throws Exception {
        // Given
        Message message1 = new Message(List.of("tag1"), "Title 1", "Author 1", 10, "2024-01-01");
        List<Message> messages = Collections.singletonList(message1);

        when(messageService.getTop3Message()).thenReturn(messages);

        // When & Then
        mockMvc.perform(get("/api/messages/top3like"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @DisplayName("GET /api/messages/top3like - Doit gérer NoFoundException avec erreur 404")
    void getTop3Like_shouldHandleNoFoundException() throws Exception {
        // Given
        when(messageService.getTop3Message())
                .thenThrow(new NoFoundException("Messages not found"));

        // When & Then
        // GlobalExceptionHandler retourne 404 NOT_FOUND pour NoFoundException
        mockMvc.perform(get("/api/messages/top3like"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Messages not found"));
    }

    @Test
    @DisplayName("GET /api/messages/top3like - Doit gérer les erreurs internes du service avec erreur 500")
    void getTop3Like_shouldHandleServiceException() throws Exception {
        // Given
        when(messageService.getTop3Message())
                .thenThrow(new RuntimeException("Database error"));

        // When & Then
        // GlobalExceptionHandler retourne 500 INTERNAL_SERVER_ERROR pour les Exception génériques
        mockMvc.perform(get("/api/messages/top3like"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Database error"));
    }

    @Test
    @DisplayName("GET /api/messages/top3like - Doit retourner exactement 3 messages quand disponibles")
    void getTop3Like_shouldReturnExactlyThreeMessagesWhenAvailable() throws Exception {
        // Given
        Message message1 = new Message(List.of("tag1"), "Title 1", "Author 1", 100, "2024-01-01");
        Message message2 = new Message(List.of("tag2"), "Title 2", "Author 2", 50, "2024-01-02");
        Message message3 = new Message(List.of("tag3"), "Title 3", "Author 3", 25, "2024-01-03");
        List<Message> messages = Arrays.asList(message1, message2, message3);

        when(messageService.getTop3Message()).thenReturn(messages);

        // When & Then
        mockMvc.perform(get("/api/messages/top3like"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3));
    }
}
