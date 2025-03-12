package com.between.products.adapter.out.rest;

import com.between.products.domain.product.Product;
import com.between.products.domain.product.ProductNotFoundException;
import com.between.products.domain.product.ProductRequestException;
import com.between.products.port.out.rest.ProductOutPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ProductService implements ProductOutPort {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);
    private final WebClient webClient;

    public ProductService(WebClient.Builder webClientBuilder, @Value("${product.api.baseUrl}") String apiBaseUrl) {
        this.webClient = webClientBuilder.baseUrl(apiBaseUrl).build();
    }

    @Override
    @Cacheable(value = "similarIds", key = "#productId")
    public Flux<Integer> getProductSimilarIds(Integer productId) {
        return webClient.get()
                .uri("/product/{productId}/similarids", productId)
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse -> handleError(new ProductRequestException(productId, clientResponse.statusCode())))
                .bodyToFlux(Integer.class);
    }

    @Override
    @Cacheable(value = "productDetail", key = "#productId")
    public Mono<Product> getProductDetail(Integer productId) {
        return webClient.get()
                .uri("/product/{productId}", productId)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> handleError(new ProductNotFoundException(productId)))
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> handleError(new ProductRequestException(productId, clientResponse.statusCode())))
                .bodyToMono(Product.class);
    }

    private <T> Mono<T> handleError(Exception exception) {
        logger.error(exception.getMessage());
        return Mono.error(exception);
    }
}
