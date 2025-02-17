package com.projet.hetic.frag.exception;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<Map<String, String>> handleUserNotFoundException(UserNotFoundException ex) {
    return ResponseEntity
        .status(404)
        .body(Map.of(
            "status", "error",
            "message", ex.getMessage()));
  }
}