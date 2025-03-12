package com.between.products.port.out.rest;

import com.between.products.domain.product.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductOutPort {
    Flux<Integer> getProductSimilarIds(Integer productId);

    Mono<Product> getProductDetail(Integer productId);
}
