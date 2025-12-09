package com.GreenThumb.api.user.application.service;

import com.GreenThumb.api.user.domain.service.PasswordMatchValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PasswordMatchValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface PasswordMatch {

    String message() default "Les champs ne correspondent pas";

    String password();

    String confirmPassword();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
