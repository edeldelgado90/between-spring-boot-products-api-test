package com.between.products.domain.product;

import org.springframework.http.HttpStatusCode;

public class ProductRequestException extends RuntimeException {
    public ProductRequestException(String message, HttpStatusCode httpStatusCode) {
        super(String.format("Error retrieving product information for id: %s. HTTP Status Code: %s", message, httpStatusCode.value()));
    }
}
