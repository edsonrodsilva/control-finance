package com.controlfinance.shared.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(ApiException.class)
  public ResponseEntity<Map<String, Object>> handleApi(ApiException ex, HttpServletRequest req) {
    log.warn("api_error code={} status={} path={} msg={}", ex.getCode(), ex.getStatus(), req.getRequestURI(), ex.getMessage());
    return ResponseEntity.status(ex.getStatus()).body(body(ex.getStatus(), ex.getCode(), ex.getMessage(), req));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
    Map<String, String> errors = new HashMap<>();
    for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
      errors.put(fe.getField(), fe.getDefaultMessage());
    }
    Map<String, Object> b = body(HttpStatus.UNPROCESSABLE_ENTITY, "VALIDATION_ERROR", "Validation failed", req);
    b.put("fields", errors);
    return ResponseEntity.unprocessableEntity().body(b);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<Map<String, Object>> handleConstraint(ConstraintViolationException ex, HttpServletRequest req) {
    return ResponseEntity.unprocessableEntity().body(body(HttpStatus.UNPROCESSABLE_ENTITY, "VALIDATION_ERROR", ex.getMessage(), req));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex, HttpServletRequest req) {
    log.error("unexpected_error path={}", req.getRequestURI(), ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(body(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "Unexpected error", req));
  }

  private Map<String, Object> body(HttpStatus status, String code, String message, HttpServletRequest req) {
    Map<String, Object> b = new HashMap<>();
    b.put("timestamp", Instant.now().toString());
    b.put("status", status.value());
    b.put("error", status.getReasonPhrase());
    b.put("code", code);
    b.put("message", message);
    b.put("path", req.getRequestURI());
    return b;
  }
}
