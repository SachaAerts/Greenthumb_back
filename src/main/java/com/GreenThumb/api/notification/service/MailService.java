package com.GreenThumb.api.notification.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender mailSender;

    public void send(String to, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);
            helper.setFrom("greenthumb.application@gmail.com");

            mailSender.send(message);
            log.info("Email HTML envoyé avec succès à {}", to);
        } catch (MessagingException e) {
            log.error("Erreur lors de l'envoi de l'email à {}: {}", to, e.getMessage());
            throw new RuntimeException("Impossible d'envoyer l'email", e);
        }
    }
}
