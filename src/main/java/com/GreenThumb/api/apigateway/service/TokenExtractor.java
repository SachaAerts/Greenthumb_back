package com.GreenThumb.api.apigateway.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service responsible for extracting and validating JWT tokens from Authorization headers.
 * Follows Single Responsibility Principle by focusing only on token extraction.
 */
@Slf4j
@Service
public class TokenExtractor {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final int BEARER_PREFIX_LENGTH = BEARER_PREFIX.length();

    /**
     * Extracts the JWT token from an Authorization header.
     *
     * @param authorizationHeader the Authorization header value
     * @return the extracted token
     * @throws IllegalArgumentException if the header is invalid or missing
     */
    public String extractToken(String authorizationHeader) {
        validateAuthorizationHeader(authorizationHeader);

        String token = authorizationHeader.substring(BEARER_PREFIX_LENGTH).trim();

        if (token.isEmpty()) {
            log.warn("Token is empty after '{}' prefix", BEARER_PREFIX);
            throw new IllegalArgumentException("Token cannot be empty");
        }

        return token;
    }

    /**
     * Validates the Authorization header format.
     *
     * @param authorizationHeader the header to validate
     * @throws IllegalArgumentException if the header is invalid
     */
    private void validateAuthorizationHeader(String authorizationHeader) {
        if (authorizationHeader == null || authorizationHeader.trim().isEmpty()) {
            log.warn("Authorization header is missing");
            throw new IllegalArgumentException("Authorization header is required");
        }

        if (authorizationHeader.length() < BEARER_PREFIX_LENGTH ||
                !authorizationHeader.substring(0, BEARER_PREFIX_LENGTH).equals(BEARER_PREFIX)) {
            log.warn("Invalid Authorization header format");
            throw new IllegalArgumentException("Invalid token format");
        }
    }
}
