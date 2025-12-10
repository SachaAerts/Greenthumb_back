package com.GreenThumb.api.plant.domain.exceptions;

public class TrefleApiException extends RuntimeException {
    public TrefleApiException(String message) {
        super(message);
    }

    public TrefleApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
