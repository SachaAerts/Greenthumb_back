package com.GreenThumb.api.apigateway.utils;

import com.GreenThumb.api.apigateway.utils.tags.PasswordMatch;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;

public class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, Object> {

    private String firstFieldName;
    private String secondFieldName;

    @Override
    public void initialize(PasswordMatch constraintAnnotation) {
        this.firstFieldName = constraintAnnotation.password();
        this.secondFieldName = constraintAnnotation.confirmPassword();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        try {
            BeanWrapper beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(value);

            Object firstValue = beanWrapper.getPropertyValue(firstFieldName);
            Object secondValue = beanWrapper.getPropertyValue(secondFieldName);

            if (firstValue == null) {
                return secondValue == null;
            }

            return firstValue.equals(secondValue);

        } catch (Exception e) {
            return false;
        }
    }
}