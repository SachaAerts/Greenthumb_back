package com.GreenThumb.api.forum.application.dto;

public record ModerationDecisionResponse(
        boolean success,
        String message,
        Long messageId,
        String action,
        String reviewedBy
) {
    public static ModerationDecisionResponse approved(Long messageId, String reviewedBy) {
        return new ModerationDecisionResponse(
                true,
                "Décision IA approuvée - Message reste supprimé",
                messageId,
                "APPROVED",
                reviewedBy
        );
    }

    public static ModerationDecisionResponse rejected(Long messageId, String reviewedBy) {
        return new ModerationDecisionResponse(
                true,
                "Décision IA révoquée - Message remis en ligne",
                messageId,
                "REJECTED",
                reviewedBy
        );
    }
}
