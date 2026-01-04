package com.GreenThumb.api.forum.application.event;

import com.GreenThumb.api.forum.domain.entity.Report;
import org.springframework.context.ApplicationEvent;

public class MessageReportedEvent extends ApplicationEvent {
    private final Report report;

    public MessageReportedEvent(Object source, Report report) {
        super(source);
        this.report = report;
    }

    public Report getReport() {
        return report;
    }

    public Long getMessageId() {
        return report.messageId();
    }

    public String getReporterUsername() {
        return report.reportedUsername();
    }

    public String getReason() {
        return report.reason();
    }

    @Override
    public String toString() {
        return String.format(
                "MessageReportedEvent[id=%d, messageId=%d, reporter=%s, reason=%s]",
                report.id(), report.messageId(), report.reportedUsername(), report.reason()
        );
    }
}
