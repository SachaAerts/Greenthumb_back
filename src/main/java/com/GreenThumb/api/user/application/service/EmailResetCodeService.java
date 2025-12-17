package com.GreenThumb.api.user.application.service;

import com.GreenThumb.api.notification.service.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailResetCodeService {

    private final RedisTemplate<String, String> redis;
    private final MailService mailService;

    private final Duration TTL = Duration.ofMinutes(3);

    public String sendResetCodeMail(String email) {
        String normalizedEmail = email.toLowerCase();

        String code = String.format("%06d", new SecureRandom().nextInt(1000000));
        storeCode(normalizedEmail, code);

        String htmlContent = loadEmailTemplate(code);

        mailService.send(
                normalizedEmail,
                "[GreenThumb] Code de vérification",
                htmlContent
        );

        log.info("Email de vérification envoyé à: {}", normalizedEmail);
        return code;
    }

    private void storeCode(String email, String code) {
        redis.opsForValue().set(email, code, 3, TimeUnit.MINUTES);
    }

    private String loadEmailTemplate(String code) {
        try {
            ClassPathResource resource = new ClassPathResource("templates/email-reset-code.html");
            String template = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            return template.replace("{{RESET_CODE}}", code);
        } catch (IOException e) {
            log.error("Erreur lors du chargement du template email", e);
            return String.format(
                    "Bienvenue sur GreenThumb !\n\n" +
                            "Votre code de vérification est : %s\n\n" +
                            "Ce code expirera dans 15 minutes.\n\n" +
                            "Si vous n'avez pas créé de compte, ignorez cet email.",
                    code
            );
        }
    }
}
