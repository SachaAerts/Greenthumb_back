package com.GreenThumb.api.forum.domain.repository;

import com.GreenThumb.api.forum.domain.entity.Report;
import com.GreenThumb.api.forum.domain.entity.ReportStatus;

import java.util.List;

public interface ReportRepository {
    Report save(Report report);
    Report findById(Long id);
    boolean existsByMessageIdAndReporter(Long messageId, String reporterUsername);
    long countPendingReportsByMessageId(Long messageId);
    List<Report> findByStatus(ReportStatus status);
    List<Report> findByMessageId(Long messageId);
}
