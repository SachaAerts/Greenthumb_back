package com.GreenThumb.api.apigateway.utils.tags;

import com.GreenThumb.api.apigateway.utils.PasswordConstraintValidator;
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
