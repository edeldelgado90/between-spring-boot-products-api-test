package com.between.products.adapter.in.rest;

import com.between.products.adapter.mapper.ProductMapper;
import com.between.products.application.dto.ProductDTO;
import com.between.products.port.in.ProductInPort;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/product")
@Tag(name = "products", description = "Operations related to products")
public class ProductController {

    private final ProductInPort productInPort;
    private final ProductMapper productMapper;

    public ProductController(ProductInPort productInPort, ProductMapper productMapper) {
        this.productInPort = productInPort;
        this.productMapper = productMapper;
    }


    @GetMapping("/{productId}/similar")
    @RateLimiter(name = "similarProductsLimiter")
    public Flux<ProductDTO> getSimilarProducts(@PathVariable String productId) {
        return this.productInPort.getSimilarProducts(productId)
                .switchIfEmpty(Flux.empty())
                .map(productMapper::toProductDTO);
    }
}
