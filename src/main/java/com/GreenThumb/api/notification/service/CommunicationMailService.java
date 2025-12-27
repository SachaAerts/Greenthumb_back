package com.GreenThumb.api.notification.service;

import com.GreenThumb.api.notification.dto.EmailRecipient;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class CommunicationMailService {
    private final JavaMailSender mailSender;

    @Value("${spring.mail.noreply.username}")
    private String fromEmail;

    public CommunicationMailService(@Qualifier("noReplySender") JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendAnnouncement(String to, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);
            helper.setFrom(fromEmail);

            mailSender.send(message);
            log.info("Email d'annonce envoyé avec succès à {}", to);
        } catch (MessagingException e) {
            log.error("Erreur lors de l'envoi de l'email d'annonce à {}: {}", to, e.getMessage());
            throw new RuntimeException("Impossible d'envoyer l'email d'annonce", e);
        }
    }

    public void sendBulkAnnouncements(List<String> recipients, String subject, String body) {
        int successCount = 0;
        int failureCount = 0;

        for (String recipient : recipients) {
            try {
                sendAnnouncement(recipient, subject, body);
                successCount++;
            } catch (RuntimeException e) {
                failureCount++;
                log.warn("Échec de l'envoi à {}", recipient);
            }
        }

        log.info("Envoi groupé terminé. Succès: {}, Échecs: {}", successCount, failureCount);
    }

    public BulkEmailResult sendPersonalizedBulkEmail(
        List<EmailRecipient> recipients,
        String subject,
        String plainTextContent
    ) {
        int successCount = 0;
        int failureCount = 0;
        List<String> failedEmails = new ArrayList<>();

        for (EmailRecipient recipient : recipients) {
            try {
                String personalizedHtml = loadBulkEmailTemplate(
                    subject,
                    plainTextContent,
                    getRecipientDisplayName(recipient)
                );
                sendAnnouncement(recipient.email(), subject, personalizedHtml);
                successCount++;
            } catch (RuntimeException e) {
                failureCount++;
                failedEmails.add(recipient.email());
                log.warn("Échec de l'envoi à {} ({})", recipient.email(), recipient.username());
            }
        }

        log.info("Envoi groupé personnalisé terminé. Total: {}, Succès: {}, Échecs: {}",
            recipients.size(), successCount, failureCount);

        return new BulkEmailResult(recipients.size(), successCount, failureCount, failedEmails);
    }

    private String loadBulkEmailTemplate(String subject, String plainTextContent, String recipientName) {
        try {
            ClassPathResource resource = new ClassPathResource("templates/bulk-email-template.html");
            String template = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

            String htmlContent = plainTextContent.replace("\n", "<br>");

            return template
                .replace("{{SUBJECT}}", escapeHtml(subject))
                .replace("{{CONTENT}}", htmlContent)
                .replace("{{RECIPIENT_NAME}}", escapeHtml(recipientName));
        } catch (IOException e) {
            log.error("Erreur lors du chargement du template d'email groupé", e);
            throw new RuntimeException("Impossible de charger le template d'email", e);
        }
    }

    private String getRecipientDisplayName(EmailRecipient recipient) {
        if (recipient.firstName() != null && !recipient.firstName().isBlank()) {
            return recipient.firstName();
        }
        return recipient.username();
    }

    private String escapeHtml(String text) {
        if (text == null) return "";
        return text
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#x27;");
    }

    public record BulkEmailResult(
        int totalRecipients,
        int successCount,
        int failureCount,
        List<String> failedEmails
    ) {}
}