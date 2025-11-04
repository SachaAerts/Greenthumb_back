package com.GreenThumb.api.apigateway.utils;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PasswordConstraintValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPassword  {
    String message() default "Mot de passe invalide";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
