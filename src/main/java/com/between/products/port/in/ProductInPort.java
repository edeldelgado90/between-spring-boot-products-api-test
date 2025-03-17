package com.between.products.port.in;

import com.between.products.domain.product.Product;
import reactor.core.publisher.Flux;

public interface ProductInPort {
    Flux<Product> getSimilarProducts(String productId);
}
