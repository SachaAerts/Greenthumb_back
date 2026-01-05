package com.GreenThumb.api.plant.application.service;

import com.GreenThumb.api.plant.application.dto.PageResponse;
import com.GreenThumb.api.plant.application.dto.PlantDto;
import com.GreenThumb.api.plant.application.dto.TaskDto;
import com.GreenThumb.api.plant.application.enums.TaskStatus;
import com.GreenThumb.api.plant.application.enums.TaskType;
import com.GreenThumb.api.plant.domain.entity.Plant;
import com.GreenThumb.api.plant.domain.entity.Task;
import com.GreenThumb.api.plant.domain.repository.PlantRepository;
import com.GreenThumb.api.plant.domain.repository.TaskRepository;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PlantModuleService - Tests unitaires")
class PlantModuleServiceTest {

    @Mock
    private PlantRepository plantRepository;

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private PlantModuleService plantModuleService;

    private Plant testPlant;
    private Task testTask;
    private Pageable testPageable;

    @BeforeEach
    void setUp() {
        testPlant = new Plant(
                "plant-slug-1",
                "Scientific Name 1",
                "Common Name 1",
                "http://image1.jpg",
                "Description 1",
                "Perennial",
                "Medium",
                "Full Sun",
                "Loamy",
                6.0,
                7.0,
                15,
                25,
                true,
                "Spring",
                false,
                false,
                true,
                null
        );

        testTask = new Task(
                1L,
                1L,
                "Water plant",
                "Water the plant thoroughly",
                TaskType.ARROSAGE,
                TaskStatus.PENDING,
                LocalDate.now().plusDays(1),
                "#00FF00",
                true,
                7,
                null,
                LocalDateTime.now(),
                "Common Name 1"
        );

        testPageable = PageRequest.of(0, 5);
    }

    @Test
    @DisplayName("findAll - Doit retourner toutes les plantes avec leurs tâches")
    void findAll_shouldReturnAllPlantsWithTasks() {
        // Given
        List<Plant> plants = List.of(testPlant);
        when(plantRepository.findAll()).thenReturn(plants);
        when(plantRepository.findIdBySlug("plant-slug-1")).thenReturn(1L);
        when(taskRepository.findByPlantId(1L)).thenReturn(List.of(testTask));

        // When
        List<PlantDto> result = plantModuleService.findAll();

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).commonName()).isEqualTo("Common Name 1");
        assertThat(result.get(0).tasks()).isNotEmpty();
        verify(plantRepository, times(1)).findAll();
        verify(taskRepository, times(1)).findByPlantId(1L);
    }

    @Test
    @DisplayName("findAll - Doit retourner une liste vide quand aucune plante")
    void findAll_shouldReturnEmptyListWhenNoPlants() {
        // Given
        when(plantRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<PlantDto> result = plantModuleService.findAll();

        // Then
        assertThat(result).isEmpty();
        verify(plantRepository, times(1)).findAll();
        verify(taskRepository, never()).findByPlantId(anyLong());
    }

    @Test
    @DisplayName("findAllByUser_username - Doit retourner les plantes paginées d'un utilisateur")
    void findAllByUserUsername_shouldReturnUserPlantsPaginated() {
        // Given
        String username = "testuser";
        List<Plant> plants = List.of(testPlant);
        Page<Plant> plantsPage = new PageImpl<>(plants, testPageable, 1);

        when(plantRepository.findAllByUser_username(username, testPageable)).thenReturn(plantsPage);
        when(plantRepository.findIdBySlug("plant-slug-1")).thenReturn(1L);
        when(taskRepository.findByPlantId(1L)).thenReturn(List.of(testTask));

        // When
        PageResponse<PlantDto> result = plantModuleService.findAllByUser_username(username, testPageable);

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).commonName()).isEqualTo("Common Name 1");
        verify(plantRepository, times(1)).findAllByUser_username(username, testPageable);
    }

    @Test
    @DisplayName("findAllByUser_username - Doit retourner une page vide quand l'utilisateur n'a pas de plantes")
    void findAllByUserUsername_shouldReturnEmptyPageWhenUserHasNoPlants() {
        // Given
        String username = "testuser";
        Page<Plant> emptyPage = new PageImpl<>(Collections.emptyList(), testPageable, 0);

        when(plantRepository.findAllByUser_username(username, testPageable)).thenReturn(emptyPage);

        // When
        PageResponse<PlantDto> result = plantModuleService.findAllByUser_username(username, testPageable);

        // Then
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();
        verify(plantRepository, times(1)).findAllByUser_username(username, testPageable);
        verify(taskRepository, never()).findByPlantId(anyLong());
    }

    @Test
    @DisplayName("findAllByUser_usernameAndSearch - Doit retourner les plantes filtrées par recherche")
    void findAllByUserUsernameAndSearch_shouldReturnFilteredPlants() {
        // Given
        String username = "testuser";
        String search = "Common";
        List<Plant> plants = List.of(testPlant);
        Page<Plant> plantsPage = new PageImpl<>(plants, testPageable, 1);

        when(plantRepository.findAllByUser_usernameAndSearch(username, search, testPageable))
                .thenReturn(plantsPage);
        when(plantRepository.findIdBySlug("plant-slug-1")).thenReturn(1L);
        when(taskRepository.findByPlantId(1L)).thenReturn(List.of(testTask));

        // When
        PageResponse<PlantDto> result = plantModuleService.findAllByUser_usernameAndSearch(username, search, testPageable);

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).commonName()).contains("Common");
        verify(plantRepository, times(1)).findAllByUser_usernameAndSearch(username, search, testPageable);
    }

    @Test
    @DisplayName("findAllByUser_usernameAndSearch - Doit retourner une page vide quand aucune correspondance")
    void findAllByUserUsernameAndSearch_shouldReturnEmptyPageWhenNoMatch() {
        // Given
        String username = "testuser";
        String search = "nonexistent";
        Page<Plant> emptyPage = new PageImpl<>(Collections.emptyList(), testPageable, 0);

        when(plantRepository.findAllByUser_usernameAndSearch(username, search, testPageable))
                .thenReturn(emptyPage);

        // When
        PageResponse<PlantDto> result = plantModuleService.findAllByUser_usernameAndSearch(username, search, testPageable);

        // Then
        assertThat(result.getContent()).isEmpty();
        verify(plantRepository, times(1)).findAllByUser_usernameAndSearch(username, search, testPageable);
    }

    @Test
    @DisplayName("countTask - Doit retourner le nombre total de tâches d'un utilisateur")
    void countTask_shouldReturnTotalTaskCount() {
        // Given
        Long userId = 123L;
        when(taskRepository.countTask(userId)).thenReturn(5L);

        // When
        long result = plantModuleService.countTask(userId);

        // Then
        assertThat(result).isEqualTo(5L);
        verify(taskRepository, times(1)).countTask(userId);
    }

    @Test
    @DisplayName("countTask - Doit retourner 0 quand l'utilisateur n'a pas de tâches")
    void countTask_shouldReturnZeroWhenNoTasks() {
        // Given
        Long userId = 123L;
        when(taskRepository.countTask(userId)).thenReturn(0L);

        // When
        long result = plantModuleService.countTask(userId);

        // Then
        assertThat(result).isZero();
        verify(taskRepository, times(1)).countTask(userId);
    }

    @Test
    @DisplayName("countPendingTasks - Doit retourner le nombre de tâches en attente d'un utilisateur")
    void countPendingTasks_shouldReturnPendingTaskCount() {
        // Given
        Long userId = 123L;
        when(taskRepository.countPendingTasks(userId)).thenReturn(3L);

        // When
        long result = plantModuleService.countPendingTasks(userId);

        // Then
        assertThat(result).isEqualTo(3L);
        verify(taskRepository, times(1)).countPendingTasks(userId);
    }

    @Test
    @DisplayName("countPendingTasks - Doit retourner 0 quand aucune tâche en attente")
    void countPendingTasks_shouldReturnZeroWhenNoPendingTasks() {
        // Given
        Long userId = 123L;
        when(taskRepository.countPendingTasks(userId)).thenReturn(0L);

        // When
        long result = plantModuleService.countPendingTasks(userId);

        // Then
        assertThat(result).isZero();
        verify(taskRepository, times(1)).countPendingTasks(userId);
    }

    @Test
    @DisplayName("findAll - Doit gérer les plantes sans tâches")
    void findAll_shouldHandlePlantsWithoutTasks() {
        // Given
        List<Plant> plants = List.of(testPlant);
        when(plantRepository.findAll()).thenReturn(plants);
        when(plantRepository.findIdBySlug("plant-slug-1")).thenReturn(1L);
        when(taskRepository.findByPlantId(1L)).thenReturn(Collections.emptyList());

        // When
        List<PlantDto> result = plantModuleService.findAll();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).tasks()).isEmpty();
        verify(plantRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("findAll - Doit gérer plusieurs plantes avec plusieurs tâches chacune")
    void findAll_shouldHandleMultiplePlantsWithMultipleTasks() {
        // Given
        Plant plant2 = new Plant(
                "plant-slug-2",
                "Scientific Name 2",
                "Common Name 2",
                "http://image2.jpg",
                "Description 2",
                "Annual",
                "Low",
                "Partial Shade",
                "Sandy",
                5.5,
                6.5,
                10,
                20,
                false,
                "Summer",
                true,
                false,
                false,
                null
        );

        Task task2 = new Task(
                2L,
                2L,
                "Fertilize plant",
                "Apply fertilizer",
                TaskType.FERTILISATION,
                TaskStatus.PENDING,
                LocalDate.now().plusDays(3),
                "#0000FF",
                false,
                null,
                null,
                LocalDateTime.now(),
                "Common Name 2"
        );

        List<Plant> plants = List.of(testPlant, plant2);
        when(plantRepository.findAll()).thenReturn(plants);
        when(plantRepository.findIdBySlug("plant-slug-1")).thenReturn(1L);
        when(plantRepository.findIdBySlug("plant-slug-2")).thenReturn(2L);
        when(taskRepository.findByPlantId(1L)).thenReturn(List.of(testTask));
        when(taskRepository.findByPlantId(2L)).thenReturn(List.of(task2));

        // When
        List<PlantDto> result = plantModuleService.findAll();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).tasks()).hasSize(1);
        assertThat(result.get(1).tasks()).hasSize(1);
        verify(plantRepository, times(1)).findAll();
        verify(taskRepository, times(1)).findByPlantId(1L);
        verify(taskRepository, times(1)).findByPlantId(2L);
    }
}
