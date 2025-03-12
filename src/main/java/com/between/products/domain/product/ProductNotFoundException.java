package com.between.products.domain.product;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(Integer productId) {
        super(String.format("Product with ID %d not found.", productId));
    }
}
