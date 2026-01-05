package com.GreenThumb.api.plant.application.service;

import com.GreenThumb.api.plant.application.enums.TaskStatus;
import com.GreenThumb.api.plant.application.enums.TaskType;
import com.GreenThumb.api.plant.application.events.PlantEventPublisher;
import com.GreenThumb.api.plant.infrastructure.entity.PlantEntity;
import com.GreenThumb.api.plant.infrastructure.entity.TaskEntity;
import com.GreenThumb.api.plant.infrastructure.repository.SpringDataTaskRepository;
import com.GreenThumb.api.user.infrastructure.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TaskSchedulerService - Tests unitaires")
class TaskSchedulerServiceTest {

    @Mock
    private SpringDataTaskRepository taskRepository;

    @Mock
    private PlantEventPublisher eventPublisher;

    @InjectMocks
    private TaskSchedulerService taskSchedulerService;

    private TaskEntity testTask;
    private PlantEntity testPlant;
    private UserEntity testUser;

    @BeforeEach
    void setUp() {
        testUser = UserEntity.builder()
                .id(1L)
                .username("testuser")
                .build();

        testPlant = PlantEntity.builder()
                .id(1L)
                .slug("plant-slug-1")
                .scientificName("Scientific Name")
                .commonName("Common Name")
                .user(testUser)
                .build();

        testTask = TaskEntity.builder()
                .id(1L)
                .title("Water plant")
                .description("Water the plant")
                .taskType(TaskType.ARROSAGE)
                .status(TaskStatus.PENDING)
                .endDate(LocalDate.now())
                .color("#00FF00")
                .isRecurrent(true)
                .recurrenceFrequency(7)
                .plant(testPlant)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("checkDueTasks - Doit publier des événements pour les tâches dues aujourd'hui")
    void checkDueTasks_shouldPublishEventsForTasksDueToday() {
        // Given
        LocalDate today = LocalDate.now();
        List<TaskEntity> dueTasks = List.of(testTask);

        when(taskRepository.findByStatusAndEndDate(TaskStatus.PENDING, today))
                .thenReturn(dueTasks);
        doNothing().when(eventPublisher).publishTaskDue(
                anyLong(), anyLong(), anyLong(), anyString(), anyString(), any(TaskType.class), any(LocalDate.class)
        );

        // When
        taskSchedulerService.checkDueTasks();

        // Then
        verify(taskRepository, times(1)).findByStatusAndEndDate(TaskStatus.PENDING, today);
        verify(eventPublisher, times(1)).publishTaskDue(
                eq(1L),
                eq(1L),
                eq(1L),
                eq("Water plant"),
                eq("Common Name"),
                eq(TaskType.ARROSAGE),
                eq(today)
        );
    }

    @Test
    @DisplayName("checkDueTasks - Ne doit rien faire quand aucune tâche n'est due")
    void checkDueTasks_shouldDoNothingWhenNoTasksDue() {
        // Given
        LocalDate today = LocalDate.now();
        when(taskRepository.findByStatusAndEndDate(TaskStatus.PENDING, today))
                .thenReturn(Collections.emptyList());

        // When
        taskSchedulerService.checkDueTasks();

        // Then
        verify(taskRepository, times(1)).findByStatusAndEndDate(TaskStatus.PENDING, today);
        verify(eventPublisher, never()).publishTaskDue(
                anyLong(), anyLong(), anyLong(), anyString(), anyString(), any(TaskType.class), any(LocalDate.class)
        );
    }

    @Test
    @DisplayName("checkDueTasks - Doit gérer plusieurs tâches dues le même jour")
    void checkDueTasks_shouldHandleMultipleTasksDueSameDay() {
        // Given
        LocalDate today = LocalDate.now();
        TaskEntity task2 = TaskEntity.builder()
                .id(2L)
                .title("Fertilize plant")
                .description("Apply fertilizer")
                .taskType(TaskType.FERTILISATION)
                .status(TaskStatus.PENDING)
                .endDate(today)
                .color("#0000FF")
                .plant(testPlant)
                .createdAt(LocalDateTime.now())
                .build();

        List<TaskEntity> dueTasks = List.of(testTask, task2);

        when(taskRepository.findByStatusAndEndDate(TaskStatus.PENDING, today))
                .thenReturn(dueTasks);
        doNothing().when(eventPublisher).publishTaskDue(
                anyLong(), anyLong(), anyLong(), anyString(), anyString(), any(TaskType.class), any(LocalDate.class)
        );

        // When
        taskSchedulerService.checkDueTasks();

        // Then
        verify(taskRepository, times(1)).findByStatusAndEndDate(TaskStatus.PENDING, today);
        verify(eventPublisher, times(2)).publishTaskDue(
                anyLong(), anyLong(), anyLong(), anyString(), anyString(), any(TaskType.class), any(LocalDate.class)
        );
    }

    @Test
    @DisplayName("checkDueTasks - Doit continuer même si un événement échoue")
    void checkDueTasks_shouldContinueEvenIfEventPublishingFails() {
        // Given
        LocalDate today = LocalDate.now();
        TaskEntity task2 = TaskEntity.builder()
                .id(2L)
                .title("Fertilize plant")
                .taskType(TaskType.FERTILISATION)
                .status(TaskStatus.PENDING)
                .endDate(today)
                .plant(testPlant)
                .createdAt(LocalDateTime.now())
                .build();

        List<TaskEntity> dueTasks = List.of(testTask, task2);

        when(taskRepository.findByStatusAndEndDate(TaskStatus.PENDING, today))
                .thenReturn(dueTasks);

        doThrow(new RuntimeException("Event publishing failed"))
                .when(eventPublisher).publishTaskDue(
                        eq(1L), anyLong(), anyLong(), anyString(), anyString(), any(TaskType.class), any(LocalDate.class)
                );

        // When
        taskSchedulerService.checkDueTasks();

        // Then
        verify(taskRepository, times(1)).findByStatusAndEndDate(TaskStatus.PENDING, today);
        // Vérifie que les deux tâches ont été traitées malgré l'échec de la première
        verify(eventPublisher, times(2)).publishTaskDue(
                anyLong(), anyLong(), anyLong(), anyString(), anyString(), any(TaskType.class), any(LocalDate.class)
        );
    }

    @Test
    @DisplayName("markOverdueTasks - Doit marquer les tâches en retard comme OVERDUE")
    void markOverdueTasks_shouldMarkTasksAsOverdue() {
        // Given
        LocalDate yesterday = LocalDate.now().minusDays(1);
        when(taskRepository.updateStatusToOverdue(TaskStatus.PENDING, yesterday))
                .thenReturn(3);

        // When
        taskSchedulerService.markOverdueTasks();

        // Then
        verify(taskRepository, times(1)).updateStatusToOverdue(TaskStatus.PENDING, yesterday);
    }

    @Test
    @DisplayName("markOverdueTasks - Ne doit rien faire quand aucune tâche n'est en retard")
    void markOverdueTasks_shouldDoNothingWhenNoOverdueTasks() {
        // Given
        LocalDate yesterday = LocalDate.now().minusDays(1);
        when(taskRepository.updateStatusToOverdue(TaskStatus.PENDING, yesterday))
                .thenReturn(0);

        // When
        taskSchedulerService.markOverdueTasks();

        // Then
        verify(taskRepository, times(1)).updateStatusToOverdue(TaskStatus.PENDING, yesterday);
    }

    @Test
    @DisplayName("markOverdueTasks - Doit gérer un grand nombre de tâches en retard")
    void markOverdueTasks_shouldHandleLargeNumberOfOverdueTasks() {
        // Given
        LocalDate yesterday = LocalDate.now().minusDays(1);
        when(taskRepository.updateStatusToOverdue(TaskStatus.PENDING, yesterday))
                .thenReturn(100);

        // When
        taskSchedulerService.markOverdueTasks();

        // Then
        verify(taskRepository, times(1)).updateStatusToOverdue(TaskStatus.PENDING, yesterday);
    }

    @Test
    @DisplayName("cleanupOldCompletedTasks - Doit exécuter le nettoyage sans erreur")
    void cleanupOldCompletedTasks_shouldExecuteCleanupWithoutError() {
        // When
        taskSchedulerService.cleanupOldCompletedTasks();

        // Then
        // Pas d'exception levée - le test passe si la méthode s'exécute sans erreur
        verifyNoInteractions(taskRepository);
    }

    @Test
    @DisplayName("manualCheckDueTasks - Doit déclencher manuellement la vérification des tâches dues")
    void manualCheckDueTasks_shouldTriggerCheckDueTasksManually() {
        // Given
        LocalDate today = LocalDate.now();
        when(taskRepository.findByStatusAndEndDate(TaskStatus.PENDING, today))
                .thenReturn(Collections.emptyList());

        // When
        taskSchedulerService.manualCheckDueTasks();

        // Then
        verify(taskRepository, times(1)).findByStatusAndEndDate(TaskStatus.PENDING, today);
    }

    @Test
    @DisplayName("manualMarkOverdueTasks - Doit déclencher manuellement le marquage des tâches en retard")
    void manualMarkOverdueTasks_shouldTriggerMarkOverdueTasksManually() {
        // Given
        LocalDate yesterday = LocalDate.now().minusDays(1);
        when(taskRepository.updateStatusToOverdue(TaskStatus.PENDING, yesterday))
                .thenReturn(0);

        // When
        taskSchedulerService.manualMarkOverdueTasks();

        // Then
        verify(taskRepository, times(1)).updateStatusToOverdue(TaskStatus.PENDING, yesterday);
    }

    @Test
    @DisplayName("checkDueTasks - Doit passer les bons paramètres à l'événement")
    void checkDueTasks_shouldPassCorrectParametersToEvent() {
        // Given
        LocalDate today = LocalDate.now();
        List<TaskEntity> dueTasks = List.of(testTask);

        when(taskRepository.findByStatusAndEndDate(TaskStatus.PENDING, today))
                .thenReturn(dueTasks);

        ArgumentCaptor<Long> taskIdCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Long> plantIdCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Long> userIdCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<String> taskTitleCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> plantNameCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<TaskType> taskTypeCaptor = ArgumentCaptor.forClass(TaskType.class);
        ArgumentCaptor<LocalDate> dueDateCaptor = ArgumentCaptor.forClass(LocalDate.class);

        // When
        taskSchedulerService.checkDueTasks();

        // Then
        verify(eventPublisher, times(1)).publishTaskDue(
                taskIdCaptor.capture(),
                plantIdCaptor.capture(),
                userIdCaptor.capture(),
                taskTitleCaptor.capture(),
                plantNameCaptor.capture(),
                taskTypeCaptor.capture(),
                dueDateCaptor.capture()
        );

        assertThat(taskIdCaptor.getValue()).isEqualTo(1L);
        assertThat(plantIdCaptor.getValue()).isEqualTo(1L);
        assertThat(userIdCaptor.getValue()).isEqualTo(1L);
        assertThat(taskTitleCaptor.getValue()).isEqualTo("Water plant");
        assertThat(plantNameCaptor.getValue()).isEqualTo("Common Name");
        assertThat(taskTypeCaptor.getValue()).isEqualTo(TaskType.ARROSAGE);
        assertThat(dueDateCaptor.getValue()).isEqualTo(today);
    }
}
