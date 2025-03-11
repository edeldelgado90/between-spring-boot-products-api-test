package com.between.products.domain.product;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(String productId) {
        super(String.format("Product with ID %s not found.", productId));
    }
}
