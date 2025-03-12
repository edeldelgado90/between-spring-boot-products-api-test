package com.between.products.application.config;

import com.between.products.domain.error.ErrorResponse;
import com.between.products.domain.product.ProductNotFoundException;
import com.between.products.domain.product.ProductRequestException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();

    @Test
    public void handleProductNotFoundExceptionReturns404() {
        // Arrange
        ProductNotFoundException exception = new ProductNotFoundException(1);

        // Act
        ResponseEntity<ErrorResponse> responseEntity =
                globalExceptionHandler.handleProductNotFoundException(exception);

        ErrorResponse errorResponse = responseEntity.getBody();

        // Assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(errorResponse).isNotNull();
        assertThat(errorResponse.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(errorResponse.getError()).isEqualTo("Product with ID 1 not found.");
        assertThat(errorResponse.getTimestamp()).isBefore(LocalDateTime.now());
        assertThat(errorResponse.getRequestId()).isNotNull();
    }

    @Test
    public void handleProductRequestExceptionReturns500() {
        // Arrange
        ProductRequestException exception = new ProductRequestException(1, HttpStatus.INTERNAL_SERVER_ERROR);

        ResponseEntity<ErrorResponse> responseEntity =
                globalExceptionHandler.handleProductRequestException(exception);

        // Act
        ErrorResponse errorResponse = responseEntity.getBody();

        // Assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(errorResponse).isNotNull();
        assertThat(errorResponse.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @Test
    public void handleMethodArgumentNotValidExceptionReturns400() {
        // Arrange
        String errorMessage = "Validation error on argument!";
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        when(exception.getMessage()).thenReturn(errorMessage);

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleMethodArgumentNotValidException(exception);

        // Assert
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getBody().getStatus());
        assertEquals(errorMessage, response.getBody().getError());
        assertNotNull(response.getBody().getTimestamp());
    }
}
