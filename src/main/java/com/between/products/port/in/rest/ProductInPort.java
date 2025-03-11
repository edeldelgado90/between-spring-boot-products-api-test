package com.between.products.port.in.rest;

import com.between.products.application.dto.ProductDTO;
import reactor.core.publisher.Flux;

/**
 * Defines the contract for managing and accessing product-related operations in a
 * reactive application.
 */
public interface ProductInPort {

    /**
     * Retrieves a reactive stream containing a product similar to the one specified by its ID.
     *
     * @param productId the unique identifier of the product for which similar product is to be retrieved; must not be null or blank
     * @return a Mono emitting the matching similar product represented by {@code ProductDTO}, or an empty Mono if no match is found
     */
    Flux<ProductDTO> getSimilarProducts(String productId);
}
