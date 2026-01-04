package com.GreenThumb.api.user.application.dto;

/**
 * DTO for broadcasting user statistics updates via WebSocket.
 * Used to keep frontend in sync with backend stats (messageCount, tier, etc.) without HTTP polling.
 */
public record UserStatsUpdateDto(
        String username,
        int messageCount,
        TierDto tier,
        int countCreatedThread
) {
}
