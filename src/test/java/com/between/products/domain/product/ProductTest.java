package com.between.products.domain.product;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ProductTest {

    @Test
    void testProductBuilder() {
        // Arrange
        String id = "1234";
        String name = "Test Product";
        BigDecimal price = BigDecimal.valueOf(19.99);
        Boolean availability = true;

        // Act
        Product product = Product.builder()
                .id(id)
                .name(name)
                .price(price)
                .availability(availability)
                .build();

        // Assert
        assertNotNull(product);
        assertEquals(id, product.getId());
        assertEquals(name, product.getName());
        assertEquals(price, product.getPrice());
        assertTrue(product.getAvailability());
    }

    @Test
    void testProductEquality() {
        // Arrange
        Product product1 = Product.builder()
                .id("1234")
                .name("Product A")
                .price(BigDecimal.valueOf(10.50))
                .availability(true)
                .build();

        Product product2 = Product.builder()
                .id("1234")
                .name("Product A")
                .price(BigDecimal.valueOf(10.50))
                .availability(true)
                .build();

        // Assert
        assertEquals(product1, product2);
        assertEquals(product1.hashCode(), product2.hashCode());
    }

    @Test
    void testProductWithDifferentValues() {
        // Arrange
        Product product1 = Product.builder()
                .id("1234")
                .name("Product A")
                .price(BigDecimal.valueOf(12.50))
                .availability(true)
                .build();

        Product product2 = Product.builder()
                .id("5678")
                .name("Product B")
                .price(BigDecimal.valueOf(20.00))
                .availability(false)
                .build();

        // Assert
        assertNotEquals(product1, product2);
    }

    @Test
    void testToString() {
        // Arrange
        Product product = Product.builder()
                .id("1234")
                .name("Product A")
                .price(BigDecimal.valueOf(15.75))
                .availability(true)
                .build();

        // Act
        String productString = product.toString();

        // Assert
        assertNotNull(productString);
        assertTrue(productString.contains("1234"));
        assertTrue(productString.contains("Product A"));
        assertTrue(productString.contains("15.75"));
        assertTrue(productString.contains("true"));
    }
}