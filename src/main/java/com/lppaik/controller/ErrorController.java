package com.lppaik.controller;

import com.lppaik.model.response.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class ErrorController {

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ErrorResponse> constraintViolationException(ConstraintViolationException exception) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse.builder().error(exception.getMessage()).build());
  }

  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<ErrorResponse> responseStatusException(ResponseStatusException exception) {
    return ResponseEntity.status(exception.getStatusCode())
            .body(ErrorResponse.builder().error(exception.getReason()).build());
  }
}
