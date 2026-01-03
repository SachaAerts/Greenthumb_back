package com.GreenThumb.api.apigateway.controller.advice;

import com.GreenThumb.api.infrastructure.exception.GeminiApiException;
import com.GreenThumb.api.plant.domain.exceptions.PlantNotFoundException;
import com.GreenThumb.api.plant.domain.exceptions.TrefleApiException;
import com.GreenThumb.api.user.domain.exception.AccountNotVerifiedException;
import com.GreenThumb.api.user.domain.exception.InvalidTokenException;
import com.GreenThumb.api.user.domain.exception.NoFoundException;
import com.GreenThumb.api.user.domain.exception.UserAlreadyVerifiedException;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException exception) {
        log.warn("Validation error: {}", exception.getMessage());
        Map<String, String> errors = new HashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(error ->
            errors.put(error.getField(), error.getDefaultMessage())
        );

        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(NoFoundException.class)
    public ResponseEntity<Map<String, String>> handleNoFoundException(NoFoundException exception) {
        log.warn("Resource not found: {}", exception.getMessage());
        Map<String, String> errors = new HashMap<>();
        errors.put("error", exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errors);
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<Map<String, String>> handleInvalidTokenException(InvalidTokenException exception) {
        log.warn("Token invalide: {}", exception.getMessage());
        Map<String, String> errors = new HashMap<>();
        errors.put("error", exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(UserAlreadyVerifiedException.class)
    public ResponseEntity<Map<String, String>> handleUserAlreadyVerifiedException(UserAlreadyVerifiedException exception) {
        log.warn("Utilisateur déjà vérifié: {}", exception.getMessage());
        Map<String, String> errors = new HashMap<>();
        errors.put("error", exception.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errors);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException exception) {
        log.warn("Invalid argument: {}", exception.getMessage());
        Map<String, String> errors = new HashMap<>();
        errors.put("error", exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(AccountNotVerifiedException.class)
    public ResponseEntity<Map<String, String>> handleAccountNotVerifiedException(AccountNotVerifiedException exception) {
        log.warn("Tentative de connexion avec compte non vérifié: {}", exception.getMessage());
        Map<String, String> errors = new HashMap<>();
        errors.put("error", "account_not_verified");
        errors.put("message", exception.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errors);
    }

    @ExceptionHandler(JsonProcessingException.class)
    public ResponseEntity<Map<String, String>> handleJsonProcessingException(JsonProcessingException exception) {
        log.error("Erreur de serialisation JSON lors de la mise en cache", exception);
        Map<String, String> errors = new HashMap<>();
        errors.put("error", "Erreur de serialisation JSON lors de la mise en cache");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleHttpMessageNotReadableException(HttpMessageNotReadableException exception) {
        log.error("Erreur de désérialisation JSON", exception);

        String detailedMessage = exception.getMostSpecificCause().getMessage();
        log.error("Détails de l'erreur: {}", detailedMessage);

        Map<String, String> errors = new HashMap<>();
        errors.put("error", "Données JSON invalides");
        errors.put("message", "Le format des données envoyées est incorrect. Veuillez vérifier votre requête.");
        errors.put("details", detailedMessage);
        errors.put("hint", "Format attendu: {\"email\":\"exemple@domaine.com\"}");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(PlantNotFoundException.class)
    public ResponseEntity<Map<String, String>> handlePlantNotFoundException(PlantNotFoundException exception) {
        log.warn("Plant not found: {}", exception.getMessage());
        Map<String, String> errors = new HashMap<>();
        errors.put("error", "Plant not found");
        errors.put("message", exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errors);
    }

    @ExceptionHandler(TrefleApiException.class)
    public ResponseEntity<Map<String, String>> handleTrefleApiException(TrefleApiException exception) {
        log.error("Trefle API error: {}", exception.getMessage());
        Map<String, String> errors = new HashMap<>();
        errors.put("error", "External API error");
        errors.put("message", exception.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(errors);
    }

    @ExceptionHandler(GeminiApiException.class)
    public ResponseEntity<Map<String, String>> handleGeminiApiException(GeminiApiException exception) {
        log.error("Gemini API error: {}", exception.getMessage());
        Map<String, String> errors = new HashMap<>();
        errors.put("error", "AI Moderation Service Error");
        errors.put("message", exception.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(errors);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolationException(ConstraintViolationException exception) {
        log.warn("Constraint violation: {}", exception.getMessage());
        Map<String, String> errors = exception.getConstraintViolations()
                .stream()
                .collect(Collectors.toMap(
                        violation -> violation.getPropertyPath().toString(),
                        violation -> violation.getMessage()
                ));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleException(Exception exception) {
        log.error("Unexpected error occurred", exception);
        Map<String, String> errors = new HashMap<>();
        errors.put("error", exception.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errors);
    }
}
