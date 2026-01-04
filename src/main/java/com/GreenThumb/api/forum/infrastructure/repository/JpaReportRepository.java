package com.GreenThumb.api.forum.infrastructure.repository;

import com.GreenThumb.api.forum.domain.entity.Report;
import com.GreenThumb.api.forum.domain.entity.ReportStatus;
import com.GreenThumb.api.forum.domain.repository.ReportRepository;
import com.GreenThumb.api.forum.infrastructure.entity.ReportEntity;
import com.GreenThumb.api.forum.infrastructure.mapper.ReportMapper;
import com.GreenThumb.api.user.domain.exception.NoFoundException;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class JpaReportRepository implements ReportRepository {

    private final SpringDataReportRepository springDataRepo;

    public JpaReportRepository(
            SpringDataReportRepository springDataRepo
    ) {
        this.springDataRepo = springDataRepo;
    }

    @Override
    public Report save(Report report) {
        ReportEntity entity = ReportMapper.toEntity(report);
        ReportEntity saved = springDataRepo.save(entity);

        return ReportMapper.toDomain(saved);
    }

    @Override
    public Report findById(Long id) {
        return springDataRepo.findById(id)
                .map(ReportMapper::toDomain)
                .orElseThrow(() -> new NoFoundException("Aucun Report trouvé"));
    }

    @Override
    public boolean existsByMessageIdAndReporter(Long messageId, String reporterUsername) {
        return springDataRepo.existsByMessageIdAndReporterUsername(messageId, reporterUsername);
    }

    @Override
    public long countPendingReportsByMessageId(Long messageId) {
        return springDataRepo.countPendingReportsByMessageId(messageId);
    }

    @Override
    public List<Report> findByStatus(ReportStatus status) {
        return springDataRepo.findByStatusOrderByCreatedAtDesc(status)
                .stream()
                .map(ReportMapper::toDomain)
                .toList();
    }

    @Override
    public List<Report> findByMessageId(Long messageId) {
        return springDataRepo.findByMessageIdOrderByCreatedAtDesc(messageId)
                .stream()
                .map(ReportMapper::toDomain)
                .toList();
    }
}
