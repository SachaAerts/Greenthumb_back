package com.GreenThumb.api.forum.infrastructure.repository;

import com.GreenThumb.api.forum.domain.entity.ReportStatus;
import com.GreenThumb.api.forum.infrastructure.entity.ReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SpringDataReportRepository extends JpaRepository<ReportEntity, Long> {
    boolean existsByMessageIdAndReporterUsername(Long messageId, String reporterUsername);

    @Query("SELECT COUNT(r) FROM ReportEntity r WHERE r.messageId = :messageId AND r.status = 'PENDING'")
    long countPendingReportsByMessageId(@Param("messageId") Long messageId);

    List<ReportEntity> findByStatusOrderByCreatedAtDesc(ReportStatus status);

    List<ReportEntity> findByMessageIdOrderByCreatedAtDesc(Long messageId);
}
