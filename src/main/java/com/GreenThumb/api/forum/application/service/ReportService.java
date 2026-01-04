package com.GreenThumb.api.forum.application.service;

import com.GreenThumb.api.forum.application.event.MessageReportedEvent;
import com.GreenThumb.api.forum.domain.entity.Report;
import com.GreenThumb.api.forum.domain.entity.ReportStatus;
import com.GreenThumb.api.forum.domain.repository.MessageRepository;
import com.GreenThumb.api.forum.domain.repository.ReportRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class ReportService {
    private final ReportRepository reportRepository;
    private final MessageRepository messageRepository;
    private final ApplicationEventPublisher eventPublisher;

    public ReportService(
            ReportRepository reportRepository,
            MessageRepository messageRepository,
            ApplicationEventPublisher eventPublisher
    ) {
        this.reportRepository = reportRepository;
        this.messageRepository = messageRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public Report createReport(Long messageId, String reporterUsername, String reason) {
        if(!messageRepository.existsById(messageId)) {
            log.warn("Attempt to report non-existent message: {}", messageId);
            throw new IllegalArgumentException("Auccun message trouvé avec cette id: " + messageId);
        }

        if (reportRepository.existsByMessageIdAndReporter(messageId, reporterUsername)) {
            log.warn("User {} already reported message {}", reporterUsername, messageId);
            throw new IllegalArgumentException("Vous avez déjà reporter ce message");
        }

        Report report = Report.create(messageId, reporterUsername, reason);
        Report saveReport = reportRepository.save(report);

        log.info("Report created: id={}, messageId={}, reporter={}",
                saveReport.id(), messageId, reporterUsername);

        MessageReportedEvent event = new MessageReportedEvent(this, saveReport);
        eventPublisher.publishEvent(event);

        log.info("MessageReportedEvent published: {}", event);

        return saveReport;
    }

    public long countPendingReports(Long messageId) {
        return reportRepository.countPendingReportsByMessageId(messageId);
    }

    public List<Report> getPendingReports() {
        return reportRepository.findByStatus(ReportStatus.PENDING);
    }

    public List<Report> getReportsByMessage(Long messageId) {
        return reportRepository.findByMessageId(messageId);
    }
}
