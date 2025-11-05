package com.GreenThumb.api.apigateway.controller;

import com.GreenThumb.api.notification.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestMailController {

    private final MailService mailService;

    @GetMapping("/mail")
    public ResponseEntity<String> sendTestMail() {
        try {
            mailService.send(
                    "mvsty@proton.me",
                    "Test MailService",
                    "Ceci est un e-mail de test envoyé depuis GreenThumb !"
            );
            return ResponseEntity.ok("✅ E-mail envoyé avec succès !");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("❌ Erreur lors de l'envoi du mail : " + e.getMessage());
        }
    }
}
