package com.between.products.adapter.out.rest;

import com.between.products.domain.product.Product;
import com.between.products.domain.product.ProductNotFoundException;
import com.between.products.domain.product.ProductRequestException;
import com.between.products.port.out.rest.ProductOutPort;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
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
    private final CacheManager cacheManager;

    public ProductOutService(WebClient.Builder webClientBuilder,
                             CacheManager cacheManager,
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

        this.cacheManager = cacheManager;
    }

    @Override
    @Cacheable(value = "similarIds", key = "#productId")
    @Retry(name = "productSimilarIdsRetry", fallbackMethod = "getProductSimilarIdsFallback")
    @CircuitBreaker(name = "productSimilarIdsCB", fallbackMethod = "getProductSimilarIdsFallback")
    public Flux<Integer> getProductSimilarIds(Integer productId) {
        return webClient.get()
                .uri("/product/{productId}/similarids", productId)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> handleError(new ProductNotFoundException(productId)))
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> handleError(new ProductRequestException(productId, clientResponse.statusCode())))
                .bodyToFlux(Integer.class);
    }

    @Override
    @Cacheable(value = "productDetail", key = "#productId")
    @Retry(name = "productDetailRetry", fallbackMethod = "getProductDetailFallback")
    @CircuitBreaker(name = "productDetailCB", fallbackMethod = "getProductDetailFallback")
    public Mono<Product> getProductDetail(Integer productId) {
        return webClient.get()
                .uri("/product/{productId}", productId)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> handleError(new ProductNotFoundException(productId)))
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> handleError(new ProductRequestException(productId, clientResponse.statusCode())))
                .bodyToMono(Product.class);
    }

    public Flux<Integer> getProductSimilarIdsFallback(Integer productId, Throwable throwable) {
        logger.warn("Fallback method called for getProductSimilarIds. Product ID: {}", productId, throwable);
        if (throwable instanceof WebClientRequestException) {
            Cache cache = this.cacheManager.getCache("similarIds");
            if (cache != null) {
                cache.put(productId, Flux.empty());
            }
        }
        return Flux.empty();
    }

    public Mono<Product> getProductDetailFallback(Integer productId, Throwable throwable) {
        logger.warn("Fallback method called for getProductDetail. Product ID: {}", productId);
        if (throwable instanceof WebClientRequestException) {
            Cache cache = this.cacheManager.getCache("productDetail");
            if (cache != null) {
                cache.put(productId, Flux.empty());
            }
        }
        return Mono.empty();
    }

    private <T> Mono<T> handleError(Exception exception) {
        logger.error(exception.getMessage());
        return Mono.error(exception);
    }
}
