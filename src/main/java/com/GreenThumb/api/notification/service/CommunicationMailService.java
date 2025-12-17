package com.GreenThumb.api.notification.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

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
}