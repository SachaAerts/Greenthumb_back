package com.GreenThumb.api.forum.application.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ModerationReviewDto(
        Long messageId,
        Long threadId,
        String threadTitle,
        String author,
        String text,
        LocalDateTime createdAt,
        Integer reportCount,

        AiModerationInfoDto aiModeration,
        List<ReportInfoDto> reports
) {
    public record AiModerationInfoDto(
            Boolean valid,
            String reason,
            String category
    ) {}

    public record ReportInfoDto(
            Long reportId,
            String reporter,
            String reason,
            String status,
            LocalDateTime reportedAt,
            LocalDateTime reviewedAt,
            String reviewedBy
    ) {}
}
