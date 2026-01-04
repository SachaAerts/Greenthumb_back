package com.GreenThumb.api.forum.domain.entity;

import java.time.LocalDateTime;

public record Report(
        Long id,
        Long messageId,
        String reportedUsername,
        String reason,
        ReportStatus status,
        LocalDateTime createAt,
        LocalDateTime reviewedAt,
        String reviewedBy
) {
    public static Report create(Long messageId, String reportedUsername, String reason) {
        return new Report(
                null,
                messageId,
                reportedUsername,
                reason,
                ReportStatus.PENDING,
                LocalDateTime.now(),
                null,
                null
        );
    }

    public Report resolve(String reviewedUsername) {
        return new Report(
                this.id,
                this.messageId,
                this.reportedUsername,
                this.reason,
                ReportStatus.RESOLVED,
                this.createAt,
                LocalDateTime.now(),
                reviewedUsername
        );
    }

    public Report dismiss(String reviewedUsername) {
        return new Report(
                this.id,
                this.messageId,
                this.reportedUsername,
                this.reason,
                ReportStatus.DISMISSED,
                this.createAt,
                LocalDateTime.now(),
                reviewedUsername
        );
    }
}
