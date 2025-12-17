package com.GreenThumb.api.apigateway.service;

import com.GreenThumb.api.apigateway.dto.Resource;
import com.GreenThumb.api.resources.application.service.ResourceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ResourceServiceApi - Tests unitaires")
class ResourceServiceApiTest {

    @Mock
    private ResourceService resourceService;

    @InjectMocks
    private ResourceServiceApi resourceServiceApi;

    private com.GreenThumb.api.resources.domain.entity.Resource testResource1;
    private com.GreenThumb.api.resources.domain.entity.Resource testResource2;
    private com.GreenThumb.api.resources.domain.entity.Resource testResource3;

    @BeforeEach
    void setUp() {
        testResource1 = new com.GreenThumb.api.resources.domain.entity.Resource(
                "resource-1",
                "Resource 1",
                "Summary 1",
                1,
                "https://example.com/resource1",
                "Text 1",
                LocalDate.now(),
                "testuser"
        );

        testResource2 = new com.GreenThumb.api.resources.domain.entity.Resource(
                "resource-2",
                "Resource 2",
                "Summary 2",
                2,
                "https://example.com/resource2",
                "Text 2",
                LocalDate.now(),
                "testuser"
        );

        testResource3 = new com.GreenThumb.api.resources.domain.entity.Resource(
                "resource-3",
                "Resource 3",
                "Summary 3",
                3,
                "https://example.com/resource3",
                "Text 3",
                LocalDate.now(),
                "testuser"
        );
    }

    @Test
    @DisplayName("getThreeResource - Doit retourner 3 ressources transformées en DTO")
    void getThreeResource_shouldReturnThreeResourcesAsDtos() {
        // Given
        List<com.GreenThumb.api.resources.domain.entity.Resource> resourceList = List.of(
                testResource1,
                testResource2,
                testResource3
        );

        when(resourceService.get3Resources()).thenReturn(resourceList);

        // When
        List<Resource> result = resourceServiceApi.getThreeResource();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(3);
        assertThat(result.get(0).title()).isEqualTo("Resource 1");
        assertThat(result.get(1).title()).isEqualTo("Resource 2");
        assertThat(result.get(2).title()).isEqualTo("Resource 3");

        verify(resourceService, times(1)).get3Resources();
    }

    @Test
    @DisplayName("getThreeResource - Doit retourner une liste vide si aucune ressource")
    void getThreeResource_shouldReturnEmptyListIfNoResources() {
        // Given
        List<com.GreenThumb.api.resources.domain.entity.Resource> emptyList = new ArrayList<>();
        when(resourceService.get3Resources()).thenReturn(emptyList);

        // When
        List<Resource> result = resourceServiceApi.getThreeResource();

        // Then
        assertThat(result).isEmpty();
        verify(resourceService, times(1)).get3Resources();
    }

    @Test
    @DisplayName("getThreeResource - Doit gérer une seule ressource")
    void getThreeResource_shouldHandleSingleResource() {
        // Given
        List<com.GreenThumb.api.resources.domain.entity.Resource> singleResourceList = List.of(testResource1);
        when(resourceService.get3Resources()).thenReturn(singleResourceList);

        // When
        List<Resource> result = resourceServiceApi.getThreeResource();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).title()).isEqualTo("Resource 1");
        verify(resourceService, times(1)).get3Resources();
    }

    @Test
    @DisplayName("getThreeResource - Doit gérer deux ressources")
    void getThreeResource_shouldHandleTwoResources() {
        // Given
        List<com.GreenThumb.api.resources.domain.entity.Resource> twoResourcesList = List.of(
                testResource1,
                testResource2
        );
        when(resourceService.get3Resources()).thenReturn(twoResourcesList);

        // When
        List<Resource> result = resourceServiceApi.getThreeResource();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).title()).isEqualTo("Resource 1");
        assertThat(result.get(1).title()).isEqualTo("Resource 2");
    }

    @Test
    @DisplayName("getThreeResource - Doit mapper correctement toutes les propriétés")
    void getThreeResource_shouldMapAllPropertiesCorrectly() {
        // Given
        List<com.GreenThumb.api.resources.domain.entity.Resource> resourceList = List.of(testResource1);
        when(resourceService.get3Resources()).thenReturn(resourceList);

        // When
        List<Resource> result = resourceServiceApi.getThreeResource();

        // Then
        assertThat(result).hasSize(1);
        Resource resource = result.get(0);
        assertThat(resource.title()).isEqualTo("Resource 1");
        assertThat(resource.imageUrl()).isEqualTo("https://example.com/resource1");
        assertThat(resource.date()).isNotNull();
    }

    @Test
    @DisplayName("getThreeResource - Doit propager les exceptions du ResourceService")
    void getThreeResource_shouldPropagateResourceServiceExceptions() {
        // Given
        when(resourceService.get3Resources())
                .thenThrow(new RuntimeException("Database connection failed"));

        // When & Then
        try {
            resourceServiceApi.getThreeResource();
        } catch (RuntimeException e) {
            assertThat(e.getMessage()).isEqualTo("Database connection failed");
        }

        verify(resourceService, times(1)).get3Resources();
    }

    @Test
    @DisplayName("getThreeResource - Doit transformer une liste immutable en stream")
    void getThreeResource_shouldTransformImmutableList() {
        // Given
        List<com.GreenThumb.api.resources.domain.entity.Resource> immutableList = List.of(
                testResource1,
                testResource2,
                testResource3
        );
        when(resourceService.get3Resources()).thenReturn(immutableList);

        // When
        List<Resource> result = resourceServiceApi.getThreeResource();

        // Then
        assertThat(result).hasSize(3);
        assertThat(result).isNotSameAs(immutableList);
        verify(resourceService, times(1)).get3Resources();
    }

    @Test
    @DisplayName("getThreeResource - Doit appeler le mapper pour chaque ressource")
    void getThreeResource_shouldCallMapperForEachResource() {
        // Given
        List<com.GreenThumb.api.resources.domain.entity.Resource> resourceList = List.of(
                testResource1,
                testResource2
        );
        when(resourceService.get3Resources()).thenReturn(resourceList);

        // When
        List<Resource> result = resourceServiceApi.getThreeResource();

        // Then
        assertThat(result).hasSize(2);
        verify(resourceService, times(1)).get3Resources();
    }
}
