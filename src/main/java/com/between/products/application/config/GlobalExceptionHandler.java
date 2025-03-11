package com.between.products.application.config;

import com.between.products.domain.error.ErrorResponse;
import com.between.products.domain.product.ProductNotFoundException;
import com.between.products.domain.product.ProductRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.UUID;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProductNotFoundException(ProductNotFoundException ex) {
        return getResponseEntity(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(ProductRequestException.class)
    public ResponseEntity<ErrorResponse> handleProductRequestException(
            ProductRequestException ex) {
        return getResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex) {
        return getResponseEntity(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    private static ResponseEntity<ErrorResponse> getResponseEntity(
            HttpStatus status, String message) {
        ErrorResponse errorResponse =
                new ErrorResponse(
                        LocalDateTime.now(), status.value(), message, UUID.randomUUID().toString());
        return ResponseEntity.status(status).body(errorResponse);
    }
}
