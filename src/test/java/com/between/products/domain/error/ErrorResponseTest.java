package com.between.products.domain.error;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ErrorResponseTest {

    @Test
    void testAllArgsConstructorAndGetters() {
        // Arrange
        LocalDateTime timestamp = LocalDateTime.now();
        int status = 404;
        String error = "Not Found";
        String requestId = "1234";

        // Act
        ErrorResponse errorResponse = new ErrorResponse(timestamp, status, error, requestId);

        // Assert
        assertThat(errorResponse.getTimestamp()).isEqualTo(timestamp);
        assertThat(errorResponse.getStatus()).isEqualTo(status);
        assertThat(errorResponse.getError()).isEqualTo(error);
        assertThat(errorResponse.getRequestId()).isEqualTo(requestId);
    }

    @Test
    void testSetters() {
        // Arrange
        ErrorResponse errorResponse = new ErrorResponse(null, 0, null, null);
        LocalDateTime timestamp = LocalDateTime.now();
        int status = 500;
        String error = "Internal Server Error";
        String requestId = "5678";

        // Act
        errorResponse.setTimestamp(timestamp);
        errorResponse.setStatus(status);
        errorResponse.setError(error);
        errorResponse.setRequestId(requestId);

        // Assert
        assertThat(errorResponse.getTimestamp()).isEqualTo(timestamp);
        assertThat(errorResponse.getStatus()).isEqualTo(status);
        assertThat(errorResponse.getError()).isEqualTo(error);
        assertThat(errorResponse.getRequestId()).isEqualTo(requestId);
    }

    @Test
    void testToString() {
        // Arrange
        LocalDateTime timestamp = LocalDateTime.of(2023, 10, 27, 12, 0);
        ErrorResponse errorResponse = new ErrorResponse(timestamp, 400, "Bad Request", "abcd-1234");

        // Act
        String result = errorResponse.toString();

        // Assert
        assertThat(result).contains("ErrorResponse");
        assertThat(result).contains("timestamp=2023-10-27T12:00");
        assertThat(result).contains("status=400");
        assertThat(result).contains("error=Bad Request");
        assertThat(result).contains("requestId=abcd-1234");
    }

    @Test
    void testEqualsAndHashCode() {
        // Arrange
        LocalDateTime timestamp = LocalDateTime.now();
        ErrorResponse response1 = new ErrorResponse(timestamp, 200, "OK", "req-123");
        ErrorResponse response2 = new ErrorResponse(timestamp, 200, "OK", "req-123");

        // Act
        int hasCode1 = response1.hashCode();
        int hasCode2 = response2.hashCode();

        // Assert
        assertThat(response1).isEqualTo(response2);
        assertThat(hasCode1).isEqualTo(hasCode2);
    }

    @Test
    void testNotEqualsWithDifferentFields() {
        // Arrange
        LocalDateTime timestamp1 = LocalDateTime.now();
        LocalDateTime timestamp2 = timestamp1.plusSeconds(10);
        ErrorResponse response1 = new ErrorResponse(timestamp1, 200, "OK", "req-123");
        ErrorResponse response2 = new ErrorResponse(timestamp2, 404, "Not Found", "req-456");

        // Assert
        assertThat(response1).isNotEqualTo(response2);
    }

}