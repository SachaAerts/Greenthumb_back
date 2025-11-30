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
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final RedisTemplate<String, String> redis;
    private final MailService mailService;
    
    private static final Duration CODE_TTL = Duration.ofMinutes(15);
    
    private static final Duration GENERATION_ATTEMPTS_TTL = Duration.ofHours(1);
    
    private static final Duration VERIFICATION_ATTEMPTS_TTL = Duration.ofMinutes(15);
    
    private static final int MAX_CODE_GENERATION_PER_HOUR = 3;
    private static final int MAX_VERIFICATION_ATTEMPTS_PER_CODE = 5;

    private static final SecureRandom secureRandom = new SecureRandom();
    
    private String codeKey(String email) {
        return "email_verif:" + email.toLowerCase();
    }

    private String generationAttemptsKey(String email) {
        return "email_verif_attempts:" + email.toLowerCase();
    }

    private String verificationAttemptsKey(String email, String code) {
        return "code_verify_attempts:" + email.toLowerCase() + ":" + code;
    }
    
    public String generateCode() {
        int code = secureRandom.nextInt(900000) + 100000; 
        return String.valueOf(code);
    }
    
    public void storeCode(String email, String code) {
        redis.opsForValue().set(codeKey(email), code, CODE_TTL);
        log.info("Code de vérification stocké pour l'email: {}", email);
    }
    
    public boolean hasExceededGenerationLimit(String email) {
        String key = generationAttemptsKey(email);
        String attempts = redis.opsForValue().get(key);

        if (attempts == null) {
            return false;
        }

        return Integer.parseInt(attempts) >= MAX_CODE_GENERATION_PER_HOUR;
    }
    
    public void incrementGenerationAttempts(String email) {
        String key = generationAttemptsKey(email);
        Long newCount = redis.opsForValue().increment(key);

        if (newCount != null && newCount == 1) {
            redis.expire(key, GENERATION_ATTEMPTS_TTL);
        }

        log.info("Tentatives de génération pour {}: {}/{}", email, newCount, MAX_CODE_GENERATION_PER_HOUR);
    }
    
    public boolean hasExceededVerificationLimit(String email, String code) {
        String key = verificationAttemptsKey(email, code);
        String attempts = redis.opsForValue().get(key);

        if (attempts == null) {
            return false;
        }

        return Integer.parseInt(attempts) >= MAX_VERIFICATION_ATTEMPTS_PER_CODE;
    }
    
    public void incrementVerificationAttempts(String email, String code) {
        String key = verificationAttemptsKey(email, code);
        Long newCount = redis.opsForValue().increment(key);

        if (newCount != null && newCount == 1) {
            redis.expire(key, VERIFICATION_ATTEMPTS_TTL);
        }

        log.info("Tentatives de vérification pour {}: {}/{}", email, newCount, MAX_VERIFICATION_ATTEMPTS_PER_CODE);
    }
    
    public Optional<String> verifyAndConsumeCode(String email, String code) {
        String normalizedEmail = email.toLowerCase();
        
        if (hasExceededVerificationLimit(normalizedEmail, code)) {
            log.warn("Nombre maximum de tentatives de vérification dépassé pour: {}", normalizedEmail);
            return Optional.empty();
        }
        
        String storedCode = redis.opsForValue().get(codeKey(normalizedEmail));

        if (storedCode == null) {
            log.warn("Aucun code trouvé pour l'email: {}", normalizedEmail);
            incrementVerificationAttempts(normalizedEmail, code);
            return Optional.empty();
        }
        
        if (!storedCode.equals(code)) {
            log.warn("Code invalide pour l'email: {}", normalizedEmail);
            incrementVerificationAttempts(normalizedEmail, code);
            return Optional.empty();
        }
        
        redis.delete(codeKey(normalizedEmail));
        redis.delete(verificationAttemptsKey(normalizedEmail, code));
        log.info("Code vérifié et consommé avec succès pour: {}", normalizedEmail);

        return Optional.of(normalizedEmail);
    }
    
    
    public String sendVerificationEmail(String email, String frontendBaseUrl) {
        String normalizedEmail = email.toLowerCase();
        
        if (hasExceededGenerationLimit(normalizedEmail)) {
            throw new IllegalStateException(
                    "Vous avez atteint le nombre maximum de demandes de code. " +
                    "Veuillez réessayer dans une heure."
            );
        }
        
        String code = generateCode();
        storeCode(normalizedEmail, code);
        
        incrementGenerationAttempts(normalizedEmail);
        
        String htmlContent = loadEmailTemplate(code);

        mailService.send(
                normalizedEmail,
                "Vérifiez votre adresse email - GreenThumb",
                htmlContent
        );

        log.info("Email de vérification envoyé à: {}", normalizedEmail);
        return code;
    }
    
    private String loadEmailTemplate(String code) {
        try {
            ClassPathResource resource = new ClassPathResource("templates/email-verification.html");
            String template = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            return template.replace("{{VERIFICATION_CODE}}", code);
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
