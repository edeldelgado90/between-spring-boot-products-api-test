package com.between.products.application.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ProductDTOTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testGettersAndSetters() {
        // Arrange
        ProductDTO product = new ProductDTO(null, null, null, null);

        // Act
        product.setId("123");
        product.setName("Product Name");

        // Assert
        assertEquals("123", product.getId());
        assertEquals("Product Name", product.getName());
    }

    @Test
    void testConstructor() {
        // Arrange & Act
        ProductDTO product = new ProductDTO(
                "123",
                "Product Name",
                BigDecimal.valueOf(100.50),
                true
        );

        // Assert
        assertEquals("123", product.getId());
        assertEquals("Product Name", product.getName());
        assertEquals(BigDecimal.valueOf(100.50), product.getPrice());
        assertEquals(true, product.getAvailability());
    }

    @Test
    void testToString() {
        // Arrange
        ProductDTO product = new ProductDTO(
                "123",
                "Product Name",
                BigDecimal.valueOf(100.50),
                true
        );

        // Act
        String result = product.toString();

        // Assert
        assertTrue(result.contains("123"));
        assertTrue(result.contains("Product Name"));
        assertTrue(result.contains("100.5"));
        assertTrue(result.contains("true"));
    }

    @Test
    void testEquals() {
        // Arrange
        ProductDTO product1 = new ProductDTO(
                "123",
                "Product Name",
                BigDecimal.valueOf(100.50),
                true
        );
        ProductDTO product2 = new ProductDTO(
                "123",
                "Product Name",
                BigDecimal.valueOf(100.50),
                true
        );
        ProductDTO product3 = new ProductDTO(
                "124",
                "Other Product",
                BigDecimal.valueOf(50.00),
                false
        );

        // Act & Assert
        assertEquals(product1, product2);
        assertNotEquals(product1, product3);
    }

    @Test
    void testHashCode() {
        // Arrange
        ProductDTO product1 = new ProductDTO(
                "123",
                "Product Name",
                BigDecimal.valueOf(100.50),
                true
        );
        ProductDTO product2 = new ProductDTO(
                "123",
                "Product Name",
                BigDecimal.valueOf(100.50),
                true
        );
        ProductDTO product3 = new ProductDTO(
                "124",
                "Other Product",
                BigDecimal.valueOf(50.00),
                false
        );

        // Act & Assert
        assertEquals(product1.hashCode(), product2.hashCode());
        assertNotEquals(product1.hashCode(), product3.hashCode());
    }


    @Test
    void testValidProductDTO() {
        // Arrange
        ProductDTO product = ProductDTO.builder()
                .id("123")
                .name("Product Name")
                .price(BigDecimal.valueOf(100.50))
                .availability(true)
                .build();

        // Act
        Set<ConstraintViolation<ProductDTO>> violations = validator.validate(product);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    void testIdNotBlankConstraint() {
        // Arrange
        ProductDTO product = ProductDTO.builder()
                .id("")
                .name("Product Name")
                .price(BigDecimal.valueOf(100.50))
                .availability(true)
                .build();

        // Act
        Set<ConstraintViolation<ProductDTO>> violations = validator.validate(product);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("{product.id.NotBlank}")));
    }

    @Test
    void testNameNotBlankConstraint() {
        // Arrange
        ProductDTO product = ProductDTO.builder()
                .id("123")
                .name("")
                .price(BigDecimal.valueOf(100.50))
                .availability(true)
                .build();

        // Act
        Set<ConstraintViolation<ProductDTO>> violations = validator.validate(product);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("{product.name.NotBlank}")));
    }

    @Test
    void testPricePositiveOrZeroConstraint() {
        // Arrange
        ProductDTO product = ProductDTO.builder()
                .id("123")
                .name("Product Name")
                .price(BigDecimal.valueOf(-1))
                .availability(true)
                .build();

        // Act
        Set<ConstraintViolation<ProductDTO>> violations = validator.validate(product);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("{product.price.PositiveOrZero}")));
    }

    @Test
    void testAvailabilityNotNullConstraint() {
        // Arrange
        ProductDTO product = ProductDTO.builder()
                .id("123")
                .name("Product Name")
                .price(BigDecimal.valueOf(100.50))
                .availability(null)
                .build();

        // Act
        Set<ConstraintViolation<ProductDTO>> violations = validator.validate(product);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("{product.availability.NotNull}")));
    }
}