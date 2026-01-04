package com.GreenThumb.api.apigateway.service;

import com.GreenThumb.api.infrastructure.service.RedisService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RedisService - Tests unitaires")
class RedisServiceTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private RedisService redisService;

    @BeforeEach
    void setUp() {
        // Empty setup - mocks configured per test
    }

    @Test
    @DisplayName("save - Doit sauvegarder une valeur dans Redis")
    void save_shouldSaveValueInRedis() {
        // Given
        String key = "test-key";
        String value = "test-value";
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        // When
        redisService.save(key, value);

        // Then
        verify(redisTemplate, times(1)).opsForValue();
        verify(valueOperations, times(1)).set(key, value);
    }

    @Test
    @DisplayName("saveJson - Doit sauvegarder un JSON dans Redis")
    void saveJson_shouldSaveJsonInRedis() {
        // Given
        String key = "test-key";
        String json = "{\"name\":\"test\"}";
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        // When
        redisService.saveJson(key, json);

        // Then
        verify(redisTemplate, times(1)).opsForValue();
        verify(valueOperations, times(1)).set(key, json);
    }

    @Test
    @DisplayName("expiry - Doit définir l'expiration d'une clé")
    void expiry_shouldSetKeyExpiration() {
        // Given
        String key = "test-key";
        long duration = 5L;
        TimeUnit unit = TimeUnit.MINUTES;

        // When
        redisService.expiry(key, duration, unit);

        // Then
        verify(redisTemplate, times(1)).expire(key, duration, unit);
    }

    @Test
    @DisplayName("get - Doit récupérer une valeur depuis Redis")
    void get_shouldRetrieveValueFromRedis() {
        // Given
        String key = "test-key";
        String expectedValue = "test-value";
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(key)).thenReturn(expectedValue);

        // When
        String result = redisService.get(key);

        // Then
        assertThat(result).isEqualTo(expectedValue);
        verify(redisTemplate, times(1)).opsForValue();
        verify(valueOperations, times(1)).get(key);
    }

    @Test
    @DisplayName("get - Doit retourner null si la clé n'existe pas")
    void get_shouldReturnNullIfKeyDoesNotExist() {
        // Given
        String key = "non-existent-key";
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(key)).thenReturn(null);

        // When
        String result = redisService.get(key);

        // Then
        assertThat(result).isNull();
        verify(valueOperations, times(1)).get(key);
    }

    @Test
    @DisplayName("delete - Doit supprimer une clé de Redis")
    void delete_shouldDeleteKeyFromRedis() {
        // Given
        String key = "test-key";

        // When
        redisService.delete(key);

        // Then
        verify(redisTemplate, times(1)).delete(key);
    }

    @Test
    @DisplayName("checkKey - Doit retourner true si la clé existe")
    void checkKey_shouldReturnTrueIfKeyExists() {
        // Given
        String key = "test-key";
        when(redisTemplate.hasKey(key)).thenReturn(true);

        // When
        boolean exists = redisService.checkKey(key);

        // Then
        assertThat(exists).isTrue();
        verify(redisTemplate, times(1)).hasKey(key);
    }

    @Test
    @DisplayName("checkKey - Doit retourner false si la clé n'existe pas")
    void checkKey_shouldReturnFalseIfKeyDoesNotExist() {
        // Given
        String key = "non-existent-key";
        when(redisTemplate.hasKey(key)).thenReturn(false);

        // When
        boolean exists = redisService.checkKey(key);

        // Then
        assertThat(exists).isFalse();
        verify(redisTemplate, times(1)).hasKey(key);
    }

    @Test
    @DisplayName("save et get - Scénario complet de sauvegarde et récupération")
    void saveAndGet_shouldWorkTogether() {
        // Given
        String key = "user:123";
        String value = "John Doe";
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(key)).thenReturn(value);

        // When
        redisService.save(key, value);
        String result = redisService.get(key);

        // Then
        assertThat(result).isEqualTo(value);
        verify(valueOperations, times(1)).set(key, value);
        verify(valueOperations, times(1)).get(key);
    }

    @Test
    @DisplayName("saveJson et expiry - Doit sauvegarder un JSON avec expiration")
    void saveJsonAndExpiry_shouldSaveJsonWithExpiration() {
        // Given
        String key = "session:abc123";
        String json = "{\"user\":\"testuser\",\"role\":\"USER\"}";
        long duration = 7L;
        TimeUnit unit = TimeUnit.DAYS;
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        // When
        redisService.saveJson(key, json);
        redisService.expiry(key, duration, unit);

        // Then
        verify(valueOperations, times(1)).set(key, json);
        verify(redisTemplate, times(1)).expire(key, duration, unit);
    }

    @Test
    @DisplayName("delete - Doit supprimer une clé existante")
    void delete_shouldDeleteExistingKey() {
        // Given
        String key = "temp-key";
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(redisTemplate.hasKey(key)).thenReturn(true).thenReturn(false);

        // When
        redisService.save(key, "temporary");
        boolean existsBefore = redisService.checkKey(key);
        redisService.delete(key);
        boolean existsAfter = redisService.checkKey(key);

        // Then
        assertThat(existsBefore).isTrue();
        assertThat(existsAfter).isFalse();
        verify(redisTemplate, times(1)).delete(key);
    }

    @Test
    @DisplayName("expiry - Doit gérer différentes unités de temps")
    void expiry_shouldHandleDifferentTimeUnits() {
        // Given
        String key = "test-key";

        // When & Then - SECONDS
        redisService.expiry(key, 30L, TimeUnit.SECONDS);
        verify(redisTemplate, times(1)).expire(key, 30L, TimeUnit.SECONDS);

        // When & Then - MINUTES
        redisService.expiry(key, 5L, TimeUnit.MINUTES);
        verify(redisTemplate, times(1)).expire(key, 5L, TimeUnit.MINUTES);

        // When & Then - HOURS
        redisService.expiry(key, 2L, TimeUnit.HOURS);
        verify(redisTemplate, times(1)).expire(key, 2L, TimeUnit.HOURS);

        // When & Then - DAYS
        redisService.expiry(key, 7L, TimeUnit.DAYS);
        verify(redisTemplate, times(1)).expire(key, 7L, TimeUnit.DAYS);
    }
}
