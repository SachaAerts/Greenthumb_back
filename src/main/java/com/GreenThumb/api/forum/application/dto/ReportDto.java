package com.GreenThumb.api.forum.application.dto;

import com.GreenThumb.api.forum.domain.entity.Report;
import com.GreenThumb.api.forum.domain.entity.ReportStatus;

import java.time.LocalDateTime;

public record ReportDto(
        Long id,
        Long messageId,
        String reporterUsername,
        String reason,
        ReportStatus status,
        LocalDateTime createdAt,
        LocalDateTime reviewedAt,
        String reviewedBy
) {

    public static ReportDto fromDomain(Report report) {
        return new ReportDto(
                report.id(),
                report.messageId(),
                report.reportedUsername(),
                report.reason(),
                report.status(),
                report.createAt(),
                report.reviewedAt(),
                report.reviewedBy()
        );
    }
}
