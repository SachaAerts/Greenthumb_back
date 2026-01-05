package com.GreenThumb.api.user.application.service;

import com.GreenThumb.api.user.application.dto.TierDto;
import com.GreenThumb.api.user.domain.repository.ThreadLimitTierRepository;
import com.GreenThumb.api.user.domain.repository.UserRepository;
import com.GreenThumb.api.user.infrastructure.entity.ThreadLimitTierEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TierProgressionService - Tests unitaires")
class TierProgressionServiceTest {

    @Mock
    private ThreadLimitTierRepository threadLimitTierRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TierProgressionService tierProgressionService;

    private ThreadLimitTierEntity currentTierEntity;
    private ThreadLimitTierEntity nextTierEntity;

    @BeforeEach
    void setUp() {
        currentTierEntity = ThreadLimitTierEntity.builder()
                .idTier(1L)
                .tierName("Nouveau membre")
                .messageRequired(0)
                .threadUnlocked(3)
                .build();

        nextTierEntity = ThreadLimitTierEntity.builder()
                .idTier(2L)
                .tierName("Membre Bronze")
                .messageRequired(10)
                .threadUnlocked(5)
                .build();
    }

    @Test
    @DisplayName("checkAndUpgradeTier - Doit mettre à jour le tier quand l'utilisateur a assez de messages")
    void checkAndUpgradeTier_shouldUpgradeTierWhenEnoughMessages() {
        // Given
        Long userId = 1L;
        int messageCount = 10;
        Long currentTierId = 1L;

        when(threadLimitTierRepository.findCurrentTier(messageCount)).thenReturn(nextTierEntity);
        doNothing().when(userRepository).updateUserTier(userId, 2L);

        // When
        TierDto result = tierProgressionService.checkAndUpgradeTier(userId, messageCount, currentTierId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("Membre Bronze");
        assertThat(result.messageRequired()).isEqualTo(10);
        assertThat(result.threadUnlocked()).isEqualTo(5);
        verify(threadLimitTierRepository, times(1)).findCurrentTier(messageCount);
        verify(userRepository, times(1)).updateUserTier(userId, 2L);
    }

    @Test
    @DisplayName("checkAndUpgradeTier - Ne doit pas mettre à jour si le tier n'a pas changé")
    void checkAndUpgradeTier_shouldNotUpgradeWhenTierUnchanged() {
        // Given
        Long userId = 1L;
        int messageCount = 5;
        Long currentTierId = 1L;

        when(threadLimitTierRepository.findCurrentTier(messageCount)).thenReturn(currentTierEntity);

        // When
        TierDto result = tierProgressionService.checkAndUpgradeTier(userId, messageCount, currentTierId);

        // Then
        assertThat(result).isNull();
        verify(threadLimitTierRepository, times(1)).findCurrentTier(messageCount);
        verify(userRepository, never()).updateUserTier(anyLong(), anyLong());
    }

    @Test
    @DisplayName("checkAndUpgradeTier - Doit gérer la progression vers un tier supérieur")
    void checkAndUpgradeTier_shouldHandleProgressionToHigherTier() {
        // Given
        ThreadLimitTierEntity goldTier = ThreadLimitTierEntity.builder()
                .idTier(3L)
                .tierName("Membre Or")
                .messageRequired(50)
                .threadUnlocked(10)
                .build();

        Long userId = 1L;
        int messageCount = 50;
        Long currentTierId = 2L;

        when(threadLimitTierRepository.findCurrentTier(messageCount)).thenReturn(goldTier);
        doNothing().when(userRepository).updateUserTier(userId, 3L);

        // When
        TierDto result = tierProgressionService.checkAndUpgradeTier(userId, messageCount, currentTierId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("Membre Or");
        verify(threadLimitTierRepository, times(1)).findCurrentTier(messageCount);
        verify(userRepository, times(1)).updateUserTier(userId, 3L);
    }

    @Test
    @DisplayName("checkAndUpgradeTier - Doit retourner null quand l'utilisateur reste au même niveau")
    void checkAndUpgradeTier_shouldReturnNullWhenNoUpgrade() {
        // Given
        Long userId = 1L;
        int messageCount = 8;
        Long currentTierId = 1L;

        currentTierEntity.setIdTier(1L);
        when(threadLimitTierRepository.findCurrentTier(messageCount)).thenReturn(currentTierEntity);

        // When
        TierDto result = tierProgressionService.checkAndUpgradeTier(userId, messageCount, currentTierId);

        // Then
        assertThat(result).isNull();
        verify(threadLimitTierRepository, times(1)).findCurrentTier(messageCount);
        verify(userRepository, never()).updateUserTier(anyLong(), anyLong());
    }

    @Test
    @DisplayName("checkAndUpgradeTier - Doit mettre à jour correctement avec un grand nombre de messages")
    void checkAndUpgradeTier_shouldHandleLargeMessageCount() {
        // Given
        ThreadLimitTierEntity platinumTier = ThreadLimitTierEntity.builder()
                .idTier(5L)
                .tierName("Membre Platinum")
                .messageRequired(200)
                .threadUnlocked(20)
                .build();

        Long userId = 1L;
        int messageCount = 250;
        Long currentTierId = 4L;

        when(threadLimitTierRepository.findCurrentTier(messageCount)).thenReturn(platinumTier);
        doNothing().when(userRepository).updateUserTier(userId, 5L);

        // When
        TierDto result = tierProgressionService.checkAndUpgradeTier(userId, messageCount, currentTierId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("Membre Platinum");
        assertThat(result.messageRequired()).isEqualTo(200);
        assertThat(result.threadUnlocked()).isEqualTo(20);
        verify(threadLimitTierRepository, times(1)).findCurrentTier(messageCount);
        verify(userRepository, times(1)).updateUserTier(userId, 5L);
    }

    @Test
    @DisplayName("checkAndUpgradeTier - Doit gérer le cas limite avec 0 messages")
    void checkAndUpgradeTier_shouldHandleZeroMessages() {
        // Given
        Long userId = 1L;
        int messageCount = 0;
        Long currentTierId = 1L;

        when(threadLimitTierRepository.findCurrentTier(messageCount)).thenReturn(currentTierEntity);

        // When
        TierDto result = tierProgressionService.checkAndUpgradeTier(userId, messageCount, currentTierId);

        // Then
        assertThat(result).isNull();
        verify(threadLimitTierRepository, times(1)).findCurrentTier(messageCount);
        verify(userRepository, never()).updateUserTier(anyLong(), anyLong());
    }
}
