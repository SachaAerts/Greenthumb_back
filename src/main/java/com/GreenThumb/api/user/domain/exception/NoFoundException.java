package com.GreenThumb.api.user.domain.exception;

public class NoFoundException extends RuntimeException {

    public NoFoundException(String message) {
        super(message);
    }
}
