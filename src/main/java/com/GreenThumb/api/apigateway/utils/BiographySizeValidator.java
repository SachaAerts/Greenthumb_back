package com.GreenThumb.api.apigateway.utils;

import com.GreenThumb.api.apigateway.utils.tags.MaxWords;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class BiographySizeValidator implements ConstraintValidator<MaxWords, String> {

    private int maxWords;

    @Override
    public void initialize(MaxWords constraintAnnotation) {
        this.maxWords = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true;
        int wordCount = value.trim().isEmpty() ? 0 : value.trim().split("\\s+").length;
        return wordCount <= maxWords;
    }
}
