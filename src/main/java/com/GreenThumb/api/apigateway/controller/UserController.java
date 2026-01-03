package com.GreenThumb.api.apigateway.controller;

import com.GreenThumb.api.plant.application.dto.PageResponse;
import com.GreenThumb.api.apigateway.dto.user.CodeRequest;
import com.GreenThumb.api.apigateway.service.TokenExtractor;
import com.GreenThumb.api.user.application.dto.Passwords;
import com.GreenThumb.api.user.application.dto.UserEdit;
import com.GreenThumb.api.apigateway.service.UserServiceGateway;
import com.GreenThumb.api.apigateway.validation.PaginationValidator;
import com.GreenThumb.api.apigateway.validation.UsernameValidator;
import com.GreenThumb.api.plant.application.dto.PlantDto;
import com.GreenThumb.api.plant.application.facade.PlantFacade;
import com.GreenThumb.api.user.application.dto.UserDto;
import com.GreenThumb.api.user.domain.exception.NoFoundException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST Controller for user-related operations.
 * Handles HTTP requests for user data, plants, and current user information.
 *
 * @author GreenThumb API Team
 * @version 1.0
 * @since 1.0
 */
@Slf4j
@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final String AUTHORIZATION_HEADER = "Authorization";

    private final UserServiceGateway userService;
    private final TokenExtractor tokenExtractor;
    private final PaginationValidator paginationValidator;
    private final UsernameValidator usernameValidator;
    private final PlantFacade plantFacade;

    /**
     * Constructs a new UserController with required dependencies.
     *
     * @param userService the user service gateway
     * @param tokenExtractor the token extraction service
     * @param paginationValidator the pagination validation service
     * @param usernameValidator the username validation service
     */
    public UserController(
            UserServiceGateway userService,
            TokenExtractor tokenExtractor,
            PaginationValidator paginationValidator,
            UsernameValidator usernameValidator,
            PlantFacade plantFacade
    ) {
        this.userService = userService;
        this.tokenExtractor = tokenExtractor;
        this.paginationValidator = paginationValidator;
        this.usernameValidator = usernameValidator;
        this.plantFacade = plantFacade;
    }

    /**
     * Retrieves the total count of users in the system.
     *
     * @return HTTP 200 OK with the user count
     */
    @GetMapping("/count")
    public ResponseEntity<Long> getUserCount() {
        log.debug("Fetching user count");

        long count = userService.countUsers();

        log.debug("Found {} users", count);
        return ResponseEntity.ok(count);
    }

    /**
     * Retrieves a paginated list of plants for a specific user.
     *
     * @param username the username to fetch plants for
     * @param page the page number (default: 0, must be >= 0)
     * @param size the page size (default: 5, must be between 1 and 100)
     * @return HTTP 200 OK with a page of plants
     * @throws IllegalArgumentException if validation fails
     */
    @GetMapping("/{username}/plants")
    public ResponseEntity<PageResponse<PlantDto>> getAllPlants(
            @PathVariable String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        usernameValidator.validate(username);
        paginationValidator.validate(page, size);

        Pageable pageable = PageRequest.of(page, size);
        PageResponse<PlantDto> response = userService.getAllPlantsByUsername(username, pageable);

        return ResponseEntity.ok(response);
    }


    @GetMapping("/{username}/tasks")
    public ResponseEntity<Long> getUserTasks(
            @PathVariable String username
    ) {
        if (username == null || username.isEmpty()) {
            throw new NoFoundException("Aucun tache trouvé pour cette utilisateur");
        }

        long userId = userService.getIdByUsername(username);

        return ResponseEntity.ok(plantFacade.countTask(userId));
    }

    /**
     * Retrieves information about the currently authenticated user.
     *
     * @param authorizationHeader the Authorization header containing the Bearer token
     * @return HTTP 200 OK with the current user's information
     * @throws IllegalArgumentException if the Authorization header is invalid
     */
    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser(
            @RequestHeader(value = AUTHORIZATION_HEADER, required = false) String authorizationHeader
    ) {
        log.debug("Fetching current user information");

        String token = tokenExtractor.extractToken(authorizationHeader);

        try {
            UserDto user = userService.getMe(token);
            log.debug("Successfully fetched user information for '{}'", user.username());
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            log.error("Error fetching current user information", e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @PatchMapping("/{oldUsername}")
    public ResponseEntity<?> editUser(@PathVariable String oldUsername, @RequestBody UserEdit user) {
        try {
            userService.editUser(user, oldUsername);
            return ResponseEntity.ok(Map.of("message", "Profil mis à jour avec succès"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @PatchMapping("/{oldUsername}/password")
    public ResponseEntity<?> changeUserPassword(@PathVariable String oldUsername, @RequestBody Passwords passwords) {
        try {
            userService.editPassword(passwords, oldUsername);
            return ResponseEntity.ok(Map.of("message", "Mot de passe mis à jour avec succès"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/verification")
    public ResponseEntity<?> checkCode(@RequestBody CodeRequest request) {
        if (request == null || request.code() == null || request.code().isBlank()
                || request.email() == null || request.email().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Code et email obligatoires"));
        }

        return userService.checkResetCode(request.code(), request.email())
                ? ResponseEntity.ok().build()
                : ResponseEntity.badRequest().body(Map.of("error", "Code incorrect"));
    }

    @PatchMapping("/{email}/resetPassword")
    public ResponseEntity<?> changePassword(
            @PathVariable String email,
            @Valid @RequestBody Passwords passwords
    ) {
        userService.resetPassword(passwords, email);

        return  ResponseEntity.ok().body(Map.of("sucess", "Mot de passe modifier avec succes"));
    }
    
    @DeleteMapping("/me")
    public ResponseEntity<?> deactivateOwnAccount(Authentication authentication) {
        String username = authentication.getName();
        log.debug("User {} requesting self-deactivation", username);

        try {
            if (userService.isAdmin(username)) {
                log.warn("Admin {} attempted to self-deactivate (blocked)", username);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message", "Les administrateurs ne peuvent pas désactiver leur propre compte"));
            }

            userService.deactivateUser(username);

            log.info("User {} successfully deactivated and anonymized their account", username);
            return ResponseEntity.ok(Map.of(
                "message", "Votre compte a été désactivé définitivement. Vos données ont été anonymisées. Cette action est irréversible.",
                "permanent", true
            ));
        } catch (NoFoundException e) {
            log.error("User {} not found during self-deactivation", username);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Utilisateur non trouvé"));
        } catch (IllegalStateException e) {
            log.warn("User {} already deactivated", username);
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", e.getMessage()));
        }
    }
}
