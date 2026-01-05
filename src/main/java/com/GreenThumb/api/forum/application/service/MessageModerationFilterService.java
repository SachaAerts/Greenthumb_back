package com.GreenThumb.api.forum.application.service;

import com.GreenThumb.api.forum.domain.entity.Message;
import com.GreenThumb.api.forum.domain.entity.Report;
import com.GreenThumb.api.forum.domain.entity.ReportStatus;
import com.GreenThumb.api.forum.domain.repository.ReportRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class MessageModerationFilterService {

    private final ReportRepository reportRepository;

    public MessageModerationFilterService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }


    public boolean isMessageVisible(Message message) {
        log.info("🔍 FILTRAGE - Message {}, aiModerationValid={}", message.id(), message.aiModerationValid());

        if (message.aiModerationValid() == null || message.aiModerationValid()) {
            log.info("✅ Message {} AFFICHÉ (valid={})", message.id(), message.aiModerationValid());
            return true;
        }

        log.info("⚠️ Message {} invalidé par IA, vérification du report...", message.id());
        List<Report> reports = reportRepository.findByMessageId(message.id());

        if (reports.isEmpty()) {
            log.info("❌ Message {} MASQUÉ (aucun report trouvé)", message.id());
            return false;
        }

        Report latestReport = reports.get(reports.size() - 1);
        log.info("📋 Message {} a un report avec status={}", message.id(), latestReport.status());

        if (latestReport.status() == ReportStatus.DISMISSED) {
            log.info("✅ Message {} AFFICHÉ (report DISMISSED)", message.id());
            return true;
        }

        log.info("❌ Message {} MASQUÉ (report {})", message.id(), latestReport.status());
        return false;
    }
}