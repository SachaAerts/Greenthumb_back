package com.GreenThumb.api.apigateway.controller;

import com.GreenThumb.api.user.application.dto.AdminUserDto;
import com.GreenThumb.api.user.application.service.UserService;
import com.GreenThumb.api.user.domain.exception.NoFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<Page<AdminUserDto>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "all") String filter
    ) {
        log.debug("Admin fetching users - page: {}, size: {}, filter: {}", page, size, filter);

        Pageable pageable = PageRequest.of(page, size);
        Page<AdminUserDto> users;

        switch (filter.toLowerCase()) {
            case "active" -> users = userService.findActiveUsers(pageable);
            case "deleted" -> users = userService.findDeletedUsers(pageable);
            default -> users = userService.findAllUsers(pageable);
        }

        log.debug("Found {} users total", users.getTotalElements());
        return ResponseEntity.ok(users);
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
}