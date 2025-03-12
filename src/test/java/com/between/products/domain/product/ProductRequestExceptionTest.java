package com.between.products.domain.product;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ProductRequestExceptionTest {

    @Test
    void shouldCreateProductRequestExceptionWithCorrectMessage() {
        // Arrange
        Integer productId = 123;
        HttpStatus httpStatusCode = HttpStatus.NOT_FOUND;

        // Act
        ProductRequestException exception = new ProductRequestException(productId, httpStatusCode);

        // Assert
        String expectedMessage = "Error retrieving product information for id: 123. HTTP Status Code: 404";
        assertThat(exception.getMessage())
                .isEqualTo(expectedMessage);
    }
}