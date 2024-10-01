package com.fedkoroma.security.exception;

import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // Обработка ошибок валидации аргументов метода
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            String fieldName = "message";
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.badRequest().body(errors);
    }

    // Обработка исключений, связанных с недействительной подписью JWT
    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<Map<String, String>> handleSignatureException(SignatureException ex) {
        log.error("JWT signature exception: ", ex);
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("message", "Invalid JWT signature");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    // Обработка кастомных исключений для неподтвержденного аккаунта
    @ExceptionHandler(AccountNotConfirmedException.class)
    public ResponseEntity<Map<String, String>> handleAccountNotConfirmedException(AccountNotConfirmedException ex) {
        log.error("Account not confirmed exception: ", ex);
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    // Обработка кастомных исключений для заблокированного аккаунта
    @ExceptionHandler(AccountLockedException.class)
    public ResponseEntity<Map<String, String>> handleAccountLockedException(AccountLockedException ex) {
        log.error("Account locked exception: ", ex);
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    // Обработка кастомных исключений для недействительного доступа
    @ExceptionHandler(InvalidAccessException.class)
    public ResponseEntity<Map<String, String>> handleInvalidAccessException(InvalidAccessException ex) {
        log.error("Invalid access exception: ", ex);
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    // Обработка всех остальных исключений
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneralException(Exception ex) {
        log.error("General exception: ", ex);
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("message", "An error occurred");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
