package com.GreenThumb.api.apigateway.controller;

import com.GreenThumb.api.apigateway.service.TokenExtractor;
import com.GreenThumb.api.user.application.dto.UserEdit;
import com.GreenThumb.api.apigateway.service.UserServiceGateway;
import com.GreenThumb.api.apigateway.validation.PaginationValidator;
import com.GreenThumb.api.apigateway.validation.UsernameValidator;
import com.GreenThumb.api.plant.application.dto.PlantDto;
import com.GreenThumb.api.plant.application.facade.PlantFacade;
import com.GreenThumb.api.user.application.dto.UserDto;
import com.GreenThumb.api.user.domain.exception.NoFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
    public ResponseEntity<Page<PlantDto>> getAllPlants(
            @PathVariable String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        log.debug("Fetching plants for user '{}' - page: {}, size: {}", username, page, size);

        usernameValidator.validate(username);
        paginationValidator.validate(page, size);

        Page<PlantDto> plants = fetchUserPlants(username, page, size);

        log.debug("Found {} plants for user '{}'", plants.getTotalElements(), username);
        return ResponseEntity.ok(plants);
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
            System.out.println(user.avatar());
            userService.editUser(user, oldUsername);
            return ResponseEntity.ok(Map.of("message", "Profil mis à jour avec succès"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        }
    }
    /**
     * Fetches plants for a user with proper exception handling.
     * Wraps exceptions with contextual information about the user.
     *
     * @param username the username
     * @param page the page number
     * @param size the page size
     * @return the page of plants
     * @throws RuntimeException if fetching plants fails
     */
    private Page<PlantDto> fetchUserPlants(String username, int page, int size) {
        try {
            return userService.getAllPlantsByUsername(username, page, size);
        } catch (Exception e) {
            log.error("Error fetching plants for user '{}'", username, e);
            throw new RuntimeException("Failed to fetch plants for user: " + username, e);
        }
    }
}
