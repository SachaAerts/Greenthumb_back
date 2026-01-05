package com.GreenThumb.api.forum.application.dto;

public record ReactionBroadcastDto(
        String action,
        Long idMessage,
        ReactionDto reaction
) {
}
