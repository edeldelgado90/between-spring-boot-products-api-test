package com.between.products.domain.product;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProductNotFoundExceptionTest {

    @Test
    void shouldThrowExceptionWithExpectedMessage() {
        // Arrange
        Integer productId = 123;
        String expectedMessage = "Product with ID 123 not found.";

        // Act
        ProductNotFoundException exception =
                assertThrows(ProductNotFoundException.class, () -> {
                    throw new ProductNotFoundException(productId);
                });

        // Assert
        assertEquals(expectedMessage, exception.getMessage(),
                "Exception message should match the expected format.");
    }

    @Test
    void shouldHandleNullProductId() {
        // Act
        ProductNotFoundException exception =
                assertThrows(ProductNotFoundException.class, () -> {
                    throw new ProductNotFoundException(null);
                });

        // Assert
        assertEquals("Product with ID null not found.", exception.getMessage(),
                "Exception message should handle null productId.");
    }
}