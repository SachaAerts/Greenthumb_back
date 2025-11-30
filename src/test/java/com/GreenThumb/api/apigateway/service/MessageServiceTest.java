package com.GreenThumb.api.apigateway.service;

import com.GreenThumb.api.apigateway.dto.Message;
import com.GreenThumb.api.forum.application.service.CommentaryService;
import com.GreenThumb.api.user.application.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MessageService - Tests unitaires")
class MessageServiceTest {

    @Mock
    private CommentaryService commentaryService;

    @Mock
    private UserService userService;

    @InjectMocks
    private MessageService messageService;

    private com.GreenThumb.api.forum.domain.entity.Message testMessage1;
    private com.GreenThumb.api.forum.domain.entity.Message testMessage2;
    private com.GreenThumb.api.forum.domain.entity.Message testMessage3;

    @BeforeEach
    void setUp() {
        testMessage1 = new com.GreenThumb.api.forum.domain.entity.Message(
                "Premier message",
                "Contenu du premier message",
                10,
                List.of(),
                java.time.LocalDateTime.of(2024, 1, 1, 10, 0)
        );

        testMessage2 = new com.GreenThumb.api.forum.domain.entity.Message(
                "Deuxième message",
                "Contenu du deuxième message",
                20,
                List.of(),
                java.time.LocalDateTime.of(2024, 1, 2, 10, 0)
        );

        testMessage3 = new com.GreenThumb.api.forum.domain.entity.Message(
                "Troisième message",
                "Contenu du troisième message",
                30,
                List.of(),
                java.time.LocalDateTime.of(2024, 1, 3, 10, 0)
        );
    }

    @Test
    @DisplayName("getTop3Message - Doit retourner les 3 messages les plus likés avec les usernames")
    void getTop3Message_shouldReturnTop3MessagesWithUsernames() {
        // Given
        Map<com.GreenThumb.api.forum.domain.entity.Message, Long> top3Map = new LinkedHashMap<>();
        top3Map.put(testMessage1, 101L);
        top3Map.put(testMessage2, 102L);
        top3Map.put(testMessage3, 103L);

        when(commentaryService.getTopThreeMessagesByLikeCount()).thenReturn(top3Map);
        when(userService.getUsername(101L)).thenReturn("user1");
        when(userService.getUsername(102L)).thenReturn("user2");
        when(userService.getUsername(103L)).thenReturn("user3");

        // When
        List<Message> result = messageService.getTop3Message();

        // Then
        assertThat(result).hasSize(3);
        assertThat(result.get(0).title()).isEqualTo("Premier message");
        assertThat(result.get(1).title()).isEqualTo("Deuxième message");
        assertThat(result.get(2).title()).isEqualTo("Troisième message");

        verify(commentaryService, times(1)).getTopThreeMessagesByLikeCount();
        verify(userService, times(1)).getUsername(101L);
        verify(userService, times(1)).getUsername(102L);
        verify(userService, times(1)).getUsername(103L);
    }

    @Test
    @DisplayName("getTop3Message - Doit retourner une liste vide si aucun message")
    void getTop3Message_shouldReturnEmptyListIfNoMessages() {
        // Given
        Map<com.GreenThumb.api.forum.domain.entity.Message, Long> emptyMap = new LinkedHashMap<>();
        when(commentaryService.getTopThreeMessagesByLikeCount()).thenReturn(emptyMap);

        // When
        List<Message> result = messageService.getTop3Message();

        // Then
        assertThat(result).isEmpty();
        verify(commentaryService, times(1)).getTopThreeMessagesByLikeCount();
        verify(userService, never()).getUsername(anyLong());
    }

    @Test
    @DisplayName("getTop3Message - Doit gérer un seul message")
    void getTop3Message_shouldHandleSingleMessage() {
        // Given
        Map<com.GreenThumb.api.forum.domain.entity.Message, Long> singleMap = new LinkedHashMap<>();
        singleMap.put(testMessage1, 101L);

        when(commentaryService.getTopThreeMessagesByLikeCount()).thenReturn(singleMap);
        when(userService.getUsername(101L)).thenReturn("user1");

        // When
        List<Message> result = messageService.getTop3Message();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).title()).isEqualTo("Premier message");
        verify(commentaryService, times(1)).getTopThreeMessagesByLikeCount();
        verify(userService, times(1)).getUsername(101L);
    }

    @Test
    @DisplayName("getTop3Message - Doit gérer deux messages")
    void getTop3Message_shouldHandleTwoMessages() {
        // Given
        Map<com.GreenThumb.api.forum.domain.entity.Message, Long> twoMessagesMap = new LinkedHashMap<>();
        twoMessagesMap.put(testMessage1, 101L);
        twoMessagesMap.put(testMessage2, 102L);

        when(commentaryService.getTopThreeMessagesByLikeCount()).thenReturn(twoMessagesMap);
        when(userService.getUsername(101L)).thenReturn("user1");
        when(userService.getUsername(102L)).thenReturn("user2");

        // When
        List<Message> result = messageService.getTop3Message();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).title()).isEqualTo("Premier message");
        assertThat(result.get(1).title()).isEqualTo("Deuxième message");
        verify(userService, times(2)).getUsername(anyLong());
    }

    @Test
    @DisplayName("getTop3Message - Doit appeler le mapper pour chaque message")
    void getTop3Message_shouldCallMapperForEachMessage() {
        // Given
        Map<com.GreenThumb.api.forum.domain.entity.Message, Long> top3Map = new LinkedHashMap<>();
        top3Map.put(testMessage1, 101L);
        top3Map.put(testMessage2, 102L);

        when(commentaryService.getTopThreeMessagesByLikeCount()).thenReturn(top3Map);
        when(userService.getUsername(101L)).thenReturn("user1");
        when(userService.getUsername(102L)).thenReturn("user2");

        // When
        List<Message> result = messageService.getTop3Message();

        // Then
        assertThat(result).hasSize(2);
        verify(commentaryService, times(1)).getTopThreeMessagesByLikeCount();
        verify(userService, times(2)).getUsername(anyLong());
    }

    @Test
    @DisplayName("getTop3Message - Doit propager les exceptions du CommentaryService")
    void getTop3Message_shouldPropagateCommentaryServiceExceptions() {
        // Given
        when(commentaryService.getTopThreeMessagesByLikeCount())
                .thenThrow(new RuntimeException("Database error"));

        // When & Then
        try {
            messageService.getTop3Message();
        } catch (RuntimeException e) {
            assertThat(e.getMessage()).isEqualTo("Database error");
        }

        verify(commentaryService, times(1)).getTopThreeMessagesByLikeCount();
        verify(userService, never()).getUsername(anyLong());
    }

    @Test
    @DisplayName("getTop3Message - Doit propager les exceptions du UserService")
    void getTop3Message_shouldPropagateUserServiceExceptions() {
        // Given
        Map<com.GreenThumb.api.forum.domain.entity.Message, Long> top3Map = new LinkedHashMap<>();
        top3Map.put(testMessage1, 101L);

        when(commentaryService.getTopThreeMessagesByLikeCount()).thenReturn(top3Map);
        when(userService.getUsername(101L)).thenThrow(new RuntimeException("User not found"));

        // When & Then
        try {
            messageService.getTop3Message();
        } catch (RuntimeException e) {
            assertThat(e.getMessage()).isEqualTo("User not found");
        }

        verify(commentaryService, times(1)).getTopThreeMessagesByLikeCount();
        verify(userService, times(1)).getUsername(101L);
    }
}
