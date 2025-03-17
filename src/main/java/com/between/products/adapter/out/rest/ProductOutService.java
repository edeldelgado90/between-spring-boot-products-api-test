package com.between.products.adapter.out.rest;

import com.between.products.domain.product.Product;
import com.between.products.domain.product.ProductNotFoundException;
import com.between.products.domain.product.ProductRequestException;
import com.between.products.port.out.rest.ProductOutPort;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutException;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Service
public class ProductOutService implements ProductOutPort {

    private static final Logger logger = LoggerFactory.getLogger(ProductOutService.class);
    private final WebClient webClient;

    public ProductOutService(WebClient.Builder webClientBuilder,
                             @Value("${product.api.baseUrl}") String apiBaseUrl,
                             @Value("${product.api.timeouts.connect}") int connectTimeout,
                             @Value("${product.api.timeouts.read}") int readTimeout,
                             @Value("${product.api.timeouts.write}") int writeTimeout,
                             @Value("${product.api.timeouts.response}") int responseTimeout
    ) {
        TcpClient tcpClient = TcpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeout)
                .doOnConnected(connection -> {
                    connection.addHandlerLast(new ReadTimeoutHandler(readTimeout, TimeUnit.MILLISECONDS));
                    connection.addHandlerLast(new WriteTimeoutHandler(writeTimeout, TimeUnit.MILLISECONDS));
                });

        HttpClient httpClient = HttpClient.from(tcpClient)
                .responseTimeout(Duration.ofMillis(responseTimeout));

        this.webClient = webClientBuilder
                .baseUrl(apiBaseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

    @Override
    @Cacheable(value = "similarIds", key = "#productId")
    @Retry(name = "productServiceRetry", fallbackMethod = "getProductSimilarIdsFallback")
    @CircuitBreaker(name = "productService", fallbackMethod = "getProductSimilarIdsFallback")
    public Flux<Integer> getProductSimilarIds(Integer productId) {
        return webClient.get()
                .uri("/product/{productId}/similarids", productId)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> handleError(new ProductNotFoundException(productId)))
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> handleError(new ProductRequestException(productId, clientResponse.statusCode())))
                .bodyToFlux(Integer.class)
                .onErrorMap(ReadTimeoutException.class, ex -> new ProductRequestException(productId, HttpStatus.GATEWAY_TIMEOUT))
                .onErrorMap(WriteTimeoutException.class, ex -> new ProductRequestException(productId, HttpStatus.GATEWAY_TIMEOUT));
    }

    @Override
    @Cacheable(value = "productDetail", key = "#productId")
    @Retryable(
            retryFor = {WebClientResponseException.class},
            backoff = @Backoff(delay = 2000, maxDelay = 5000, multiplier = 2)
    )
    @CircuitBreaker(name = "productService", fallbackMethod = "getProductDetailFallback")
    public Mono<Product> getProductDetail(Integer productId) {
        return webClient.get()
                .uri("/product/{productId}", productId)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> handleError(new ProductNotFoundException(productId)))
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> handleError(new ProductRequestException(productId, clientResponse.statusCode())))
                .bodyToMono(Product.class)
                .onErrorMap(ReadTimeoutException.class, ex -> new ProductRequestException(productId, HttpStatus.GATEWAY_TIMEOUT))
                .onErrorMap(WriteTimeoutException.class, ex -> new ProductRequestException(productId, HttpStatus.GATEWAY_TIMEOUT));
    }

    public Flux<Integer> getProductSimilarIdsFallback(Integer productId, Throwable throwable) {
        logger.warn("Fallback method called for getProductSimilarIds. Product ID: {}", productId, throwable);
        return Flux.empty();
    }

    public Mono<Product> getProductDetailFallback(Integer productId, Throwable throwable) {
        logger.warn("Fallback method called for getProductDetail. Product ID: {}", productId, throwable);
        return Mono.empty();
    }

    private <T> Mono<T> handleError(Exception exception) {
        logger.error(exception.getMessage());
        return Mono.error(exception);
    }
}
