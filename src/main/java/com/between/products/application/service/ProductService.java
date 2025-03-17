package com.between.products.application.service;

import com.between.products.domain.product.Product;
import com.between.products.port.in.ProductInPort;
import com.between.products.port.out.rest.ProductOutPort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class ProductService implements ProductInPort {
    private final ProductOutPort productOutPort;

    public ProductService(ProductOutPort productOutPort) {
        this.productOutPort = productOutPort;
    }

    @Override
    public Flux<Product> getSimilarProducts(String productId) {
        return productOutPort.getProductSimilarIds(Integer.valueOf(productId))
                .flatMap(productOutPort::getProductDetail);
    }
}
