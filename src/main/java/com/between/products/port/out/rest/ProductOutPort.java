package com.between.products.port.out.rest;

import com.between.products.domain.product.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductOutPort {
    Flux<String> getProductSimilarIds(String productId);

    Mono<Product> getProductDetail(String productId);
}
