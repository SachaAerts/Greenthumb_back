package com.GreenThumb.api.forum.infrastructure.mapper;

import com.GreenThumb.api.forum.domain.entity.Report;
import com.GreenThumb.api.forum.infrastructure.entity.ReportEntity;
import org.springframework.stereotype.Component;

@Component
public class ReportMapper {

    public static ReportEntity toEntity(Report report) {
        ReportEntity entity = new ReportEntity();
        entity.setId(report.id());
        entity.setMessageId(report.messageId());
        entity.setReporterUsername(report.reportedUsername());
        entity.setReason(report.reason());
        entity.setStatus(report.status());
        entity.setCreatedAt(report.createAt());
        entity.setReviewedAt(report.reviewedAt());
        entity.setReviewedBy(report.reviewedBy());
        return entity;
    }

    public static Report toDomain(ReportEntity entity) {
        return new Report(
                entity.getId(),
                entity.getMessageId(),
                entity.getReporterUsername(),
                entity.getReason(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getReviewedAt(),
                entity.getReviewedBy()
        );
    }
}
