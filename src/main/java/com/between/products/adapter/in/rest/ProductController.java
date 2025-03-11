package com.between.products.adapter.in.rest;

import com.between.products.application.dto.ProductDTO;
import com.between.products.application.mapper.ProductMapper;
import com.between.products.port.in.rest.ProductInPort;
import com.between.products.port.out.rest.ProductOutPort;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/product")
@Tag(name = "products", description = "Operations related to products")
public class ProductController implements ProductInPort {

    private final ProductOutPort productOutPort;
    private final ProductMapper productMapper;

    public ProductController(ProductOutPort productOutPort, ProductMapper productMapper) {
        this.productOutPort = productOutPort;
        this.productMapper = productMapper;
    }

    @Override
    public Flux<ProductDTO> getSimilarProducts(String productId) {
        Flux<String> productSimilarIds = productOutPort.getProductSimilarIds(productId);
        return productSimilarIds.flatMap(productOutPort::getProductDetail).map(productMapper::toProductDTO);
    }
}
