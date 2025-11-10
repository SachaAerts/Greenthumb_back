package com.GreenThumb.api.user.domain.exception;

public class PhoneNumberAlreadyUsedException extends RuntimeException {
    public PhoneNumberAlreadyUsedException(String message) {
        super(message);
    }
}
