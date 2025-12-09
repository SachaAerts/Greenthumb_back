package com.GreenThumb.api.user.application.service;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = BiographySizeValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface MaxWords {
    String message() default "Le texte ne doit pas dépasser {value} mots.";
    int value();
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

