package com.GreenThumb.api.apigateway.controller;

import com.GreenThumb.api.user.application.dto.BulkEmailRequest;
import com.GreenThumb.api.user.application.dto.BulkEmailResponse;
import com.GreenThumb.api.user.application.dto.PageResponse;
import com.GreenThumb.api.user.application.dto.AdminUserDto;
import com.GreenThumb.api.user.application.service.UserService;
import com.GreenThumb.api.user.application.service.AdminCommunicationService;
import com.GreenThumb.api.user.domain.exception.NoFoundException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;
    private final AdminCommunicationService adminCommunicationService;

    public AdminController(
        UserService userService,
        AdminCommunicationService adminCommunicationService
    ) {
        this.userService = userService;
        this.adminCommunicationService = adminCommunicationService;
    }

    @GetMapping("/search")
    public ResponseEntity<PageResponse<AdminUserDto>> searchUsers(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "all") String status,
            @RequestParam(required = false) Boolean enabled,
            @RequestParam(required = false) String role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        log.debug("Admin searching users - query: '{}', status: {}, enabled: {}, role: {}, page: {}, size: {}",
                q, status, enabled, role, page, size);

        PageResponse<AdminUserDto> result = userService.searchUsers(q, status, enabled, role, page, size);

        log.debug("Found {} users total", result.totalElements());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{username}")
    public ResponseEntity<?> getUserByUsername(@PathVariable String username) {
        log.debug("Admin fetching user details for: {}", username);

        try {
            AdminUserDto user = userService.findByUsernameForAdmin(username);
            return ResponseEntity.ok(user);
        } catch (NoFoundException e) {
            log.warn("User not found: {}", username);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Utilisateur non trouvé"));
        }
    }

    @PatchMapping("/{username}/activate")
    public ResponseEntity<?> toggleUserActivation(
            @PathVariable String username,
            @RequestParam(required = false) Boolean enabled,
            Authentication authentication
    ) {
        log.debug("Admin toggling activation for user: {}", username);

        String currentAdminUsername = authentication.getName();

        if (currentAdminUsername.equals(username)) {
            log.warn("Admin {} attempted to deactivate themselves", currentAdminUsername);
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Un administrateur ne peut pas se désactiver lui-même"));
        }

        try {
            if (userService.isAdmin(username)) {
                log.warn("Admin {} attempted to modify another admin: {}", currentAdminUsername, username);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message", "Impossible de modifier le statut d'un autre administrateur"));
            }

            AdminUserDto targetUser = userService.findByUsernameForAdmin(username);
            boolean newStatus = (enabled != null) ? enabled : !targetUser.enabled();

            userService.setUserEnabled(username, newStatus);

            log.info("Admin {} {} user {}", currentAdminUsername, newStatus ? "activated" : "deactivated", username);
            return ResponseEntity.ok(Map.of(
                    "message", newStatus ? "Utilisateur activé avec succès" : "Utilisateur désactivé avec succès",
                    "enabled", newStatus
            ));
        } catch (NoFoundException e) {
            log.warn("User not found: {}", username);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Utilisateur non trouvé"));
        }
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<?> softDeleteUser(
            @PathVariable String username,
            Authentication authentication
    ) {
        log.debug("Admin attempting to soft delete user: {}", username);

        String currentAdminUsername = authentication.getName();

        if (currentAdminUsername.equals(username)) {
            log.warn("Admin {} attempted to delete themselves", currentAdminUsername);
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Un administrateur ne peut pas se supprimer lui-même"));
        }

        try {
            if (userService.isAdmin(username)) {
                log.warn("Admin {} attempted to delete another admin: {}", currentAdminUsername, username);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message", "Impossible de supprimer un autre administrateur"));
            }

            userService.softDeleteUserByUsername(username);

            log.info("Admin {} soft deleted user {}", currentAdminUsername, username);
            return ResponseEntity.ok(Map.of("message", "Utilisateur supprimé avec succès (soft delete)"));
        } catch (NoFoundException e) {
            log.warn("User not found: {}", username);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Utilisateur non trouvé"));
        }
    }

    @DeleteMapping("/{username}/hard")
    public ResponseEntity<?> hardDeleteUser(
            @PathVariable String username,
            Authentication authentication
    ) {
        log.debug("Admin attempting to hard delete user: {}", username);

        String currentAdminUsername = authentication.getName();

        if (currentAdminUsername.equals(username)) {
            log.warn("Admin {} attempted to delete themselves", currentAdminUsername);
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Un administrateur ne peut pas se supprimer lui-même"));
        }

        try {
            if (userService.isAdmin(username)) {
                log.warn("Admin {} attempted to hard delete another admin: {}", currentAdminUsername, username);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message", "Impossible de supprimer définitivement un autre administrateur"));
            }

            userService.hardDeleteUserByUsername(username);

            log.info("Admin {} hard deleted user {}", currentAdminUsername, username);
            return ResponseEntity.ok(Map.of("message", "Utilisateur supprimé définitivement"));
        } catch (NoFoundException e) {
            log.warn("User not found: {}", username);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Utilisateur non trouvé"));
        }
    }

    @PatchMapping("/{username}/restore")
    public ResponseEntity<?> restoreUser(
            @PathVariable String username,
            Authentication authentication
    ) {
        log.debug("Admin attempting to restore user: {}", username);

        String currentAdminUsername = authentication.getName();

        try {
            userService.restoreUserByUsername(username);

            log.info("Admin {} restored user {}", currentAdminUsername, username);
            return ResponseEntity.ok(Map.of("message", "Utilisateur restauré avec succès"));
        } catch (NoFoundException e) {
            log.warn("User not found: {}", username);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Utilisateur non trouvé"));
        }
    }

    @PostMapping("/communications/send-bulk-email")
    public ResponseEntity<?> sendBulkEmail(
        @Valid @RequestBody BulkEmailRequest request,
        Authentication authentication
    ) {
        String adminUsername = authentication.getName();
        log.info("Admin {} initiating bulk email. Subject: '{}', recipientType: {}",
            adminUsername, request.subject(), request.recipientType());

        try {
            BulkEmailResponse response = adminCommunicationService.sendBulkEmail(
                request,
                adminUsername
            );

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.warn("Invalid bulk email request from admin {}: {}", adminUsername, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            log.error("Error during bulk email send by admin {}: {}", adminUsername, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Erreur lors de l'envoi groupé: " + e.getMessage()));
        }
    }
}