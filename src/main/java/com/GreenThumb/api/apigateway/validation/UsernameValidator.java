package com.GreenThumb.api.apigateway.validation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Validator for username parameters.
 * Follows Single Responsibility Principle by focusing only on username validation.
 */
@Slf4j
@Component
public class UsernameValidator {

    /**
     * Validates a username parameter.
     *
     * @param username the username to validate
     * @throws IllegalArgumentException if username is null or empty
     */
    public void validate(String username) {
        if (username == null || username.trim().isEmpty()) {
            log.warn("Username is null or empty");
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
    }
}
