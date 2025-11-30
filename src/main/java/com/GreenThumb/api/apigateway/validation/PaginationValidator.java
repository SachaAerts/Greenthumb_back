package com.GreenThumb.api.apigateway.validation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Validator for pagination parameters.
 * Follows Single Responsibility Principle by focusing only on pagination validation.
 */
@Slf4j
@Component
public class PaginationValidator {

    private static final int MIN_PAGE_NUMBER = 0;
    private static final int MIN_PAGE_SIZE = 1;
    private static final int MAX_PAGE_SIZE = 100;

    /**
     * Validates pagination parameters.
     *
     * @param page the page number (must be >= 0)
     * @param size the page size (must be between 1 and 100)
     * @throws IllegalArgumentException if parameters are invalid
     */
    public void validate(int page, int size) {
        validatePageNumber(page);
        validatePageSize(size);
    }

    /**
     * Validates the page number.
     *
     * @param page the page number to validate
     * @throws IllegalArgumentException if page number is negative
     */
    private void validatePageNumber(int page) {
        if (page < MIN_PAGE_NUMBER) {
            log.warn("Invalid page number: {}", page);
            throw new IllegalArgumentException(
                    String.format("Page number must be >= %d", MIN_PAGE_NUMBER)
            );
        }
    }

    /**
     * Validates the page size.
     *
     * @param size the page size to validate
     * @throws IllegalArgumentException if page size is out of range
     */
    private void validatePageSize(int size) {
        if (size < MIN_PAGE_SIZE || size > MAX_PAGE_SIZE) {
            log.warn("Invalid page size: {}", size);
            throw new IllegalArgumentException(
                    String.format("Page size must be between %d and %d", MIN_PAGE_SIZE, MAX_PAGE_SIZE)
            );
        }
    }

    /**
     * Gets the maximum allowed page size.
     *
     * @return the maximum page size
     */
    public int getMaxPageSize() {
        return MAX_PAGE_SIZE;
    }
}
