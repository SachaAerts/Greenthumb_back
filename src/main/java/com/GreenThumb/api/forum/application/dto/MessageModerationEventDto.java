package com.GreenThumb.api.forum.application.dto;

public record MessageModerationEventDto(
        String type,
        Long messageId,
        String reason
) {
    public static MessageModerationEventDto messageRemoved(Long messageId, String reason) {
        return new MessageModerationEventDto("MESSAGE_REMOVED", messageId, reason);
    }
}
