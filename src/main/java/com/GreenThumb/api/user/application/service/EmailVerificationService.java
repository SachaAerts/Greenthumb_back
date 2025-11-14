package com.GreenThumb.api.user.application.service;

import com.GreenThumb.api.notification.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final RedisTemplate<String, String> redis;
    private final MailService mailService;
    private static final Duration TTL = Duration.ofHours(24);

    private String key(String token) { return "email_verif:" + token; }

    public String generateToken() { return UUID.randomUUID().toString(); }

    public void storeToken(String token, String email) {
        redis.opsForValue().set(key(token), email, TTL);
    }

    public Optional<String> consumeToken(String token) {
        String email = redis.opsForValue().get(key(token));
        if (email != null) redis.delete(key(token));
        return Optional.ofNullable(email);
    }

    public String sendVerificationEmail(String email, String frontendBaseUrl) {
        String token = generateToken();
        storeToken(token, email);

        String verificationLink = frontendBaseUrl + "/verify-email?token=" + token;
        String htmlContent = loadEmailTemplate(verificationLink);

        mailService.send(
                email,
                "Vérifiez votre adresse email - GreenThumb",
                htmlContent
        );

        return token;
    }

    private String loadEmailTemplate(String verificationLink) {
        try {
            ClassPathResource resource = new ClassPathResource("templates/email-verification.html");
            String template = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            return template.replace("{{VERIFICATION_LINK}}", verificationLink);
        } catch (IOException e) {
            return String.format(
                    "Bienvenue sur GreenThumb !\n\n" +
                    "Pour activer votre compte, veuillez cliquer sur le lien suivant :\n%s\n\n" +
                    "Ce lien expirera dans 24 heures.\n\n" +
                    "Si vous n'avez pas créé de compte, ignorez cet email.",
                    verificationLink
            );
        }
    }
}
