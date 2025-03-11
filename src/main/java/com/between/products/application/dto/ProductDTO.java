package com.between.products.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ProductDTO {
    @NotBlank(message = "{product.id.NotBlank}")
    private String id;

    @NotBlank(message = "{product.name.NotBlank}")
    private String name;

    @PositiveOrZero(message = "{product.price.PositiveOrZero}")
    private final BigDecimal price;

    @NotNull(message = "{product.availability.NotNull}")
    private final Boolean availability;
}
