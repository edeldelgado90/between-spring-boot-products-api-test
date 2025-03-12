package com.between.products.domain.product;

import org.springframework.http.HttpStatusCode;

public class ProductRequestException extends RuntimeException {
    public ProductRequestException(Integer productId, HttpStatusCode httpStatusCode) {
        super(String.format("Error retrieving product information for id: %d. HTTP Status Code: %s", productId, httpStatusCode.value()));
    }
}
