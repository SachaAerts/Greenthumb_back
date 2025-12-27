package com.GreenThumb.api.user.application.service;

import com.GreenThumb.api.notification.dto.BulkEmailRequest;
import com.GreenThumb.api.notification.dto.BulkEmailResponse;
import com.GreenThumb.api.notification.dto.EmailRecipient;
import com.GreenThumb.api.notification.service.CommunicationMailService;
import com.GreenThumb.api.user.infrastructure.entity.UserEntity;
import com.GreenThumb.api.user.infrastructure.repository.SpringDataUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class AdminCommunicationService {

    private final SpringDataUserRepository userRepository;
    private final CommunicationMailService communicationMailService;

    public AdminCommunicationService(
        SpringDataUserRepository userRepository,
        CommunicationMailService communicationMailService
    ) {
        this.userRepository = userRepository;
        this.communicationMailService = communicationMailService;
    }

    /**
     * Envoie un email groupé selon le type de destinataires
     */
    public BulkEmailResponse sendBulkEmail(BulkEmailRequest request, String adminUsername) {
        List<UserEntity> recipients = getRecipients(request);

        if (recipients.isEmpty()) {
            throw new IllegalArgumentException(
                "Aucun utilisateur éligible trouvé pour le type: " + request.recipientType()
            );
        }

        // Convertir UserEntity → EmailRecipient
        List<EmailRecipient> emailRecipients = recipients.stream()
            .map(user -> new EmailRecipient(
                user.getMail(),
                user.getFirstname(),
                user.getLastname(),
                user.getUsername()
            ))
            .toList();

        CommunicationMailService.BulkEmailResult result =
            communicationMailService.sendPersonalizedBulkEmail(
                emailRecipients,
                request.subject(),
                request.content()
            );

        log.info("Admin {} completed bulk email to {}. Total: {}, Success: {}, Failed: {}",
            adminUsername, request.recipientType(),
            result.totalRecipients(), result.successCount(), result.failureCount());

        return BulkEmailResponse.of(
            result.failureCount() == 0
                ? "Envoi groupé effectué avec succès"
                : "Envoi groupé terminé avec quelques échecs",
            result.totalRecipients(),
            result.successCount(),
            result.failureCount(),
            result.failedEmails()
        );
    }

    /**
     * Récupère les destinataires selon le type
     */
    private List<UserEntity> getRecipients(BulkEmailRequest request) {
        return switch (request.recipientType()) {
            case ALL_USERS -> {
                List<UserEntity> users = userRepository.findEligibleUsersForBulkEmail();
                log.debug("Found {} eligible standard users for bulk email", users.size());
                yield users;
            }
            case STAFF_ONLY -> {
                List<UserEntity> staff = userRepository.findStaffUsersForBulkEmail();
                log.debug("Found {} staff members for bulk email", staff.size());
                yield staff;
            }
            case SPECIFIC_USERS -> {
                if (request.recipientUsernames() == null || request.recipientUsernames().isEmpty()) {
                    throw new IllegalArgumentException(
                        "La liste de destinataires ne peut pas être vide pour un envoi ciblé"
                    );
                }
                List<UserEntity> specific = userRepository.findByUsernamesForBulkEmail(
                    request.recipientUsernames()
                );
                log.debug("Found {} users from provided username list", specific.size());
                yield specific;
            }
        };
    }
}