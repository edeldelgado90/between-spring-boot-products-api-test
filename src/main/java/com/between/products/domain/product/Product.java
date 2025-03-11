package com.between.products.domain.product;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class Product {
    private final String id;
    private final String name;
    private final BigDecimal price;
    private final Boolean availability;
}
