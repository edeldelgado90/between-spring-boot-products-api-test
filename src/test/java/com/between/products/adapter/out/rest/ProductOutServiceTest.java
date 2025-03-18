package com.between.products.adapter.out.rest;

import com.between.products.domain.product.Product;
import com.between.products.domain.product.ProductNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.eq;

@ExtendWith(MockitoExtension.class)
public class ProductOutServiceTest {

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private Cache cache;

    private ProductOutService productOutService;

    @BeforeEach
    void setUp() {
        when(webClientBuilder.baseUrl(anyString())).thenReturn(webClientBuilder);
        when(webClientBuilder.clientConnector(any())).thenReturn(webClientBuilder);
        when(webClientBuilder.build()).thenReturn(webClient);

        productOutService = new ProductOutService(
                webClientBuilder,
                cacheManager,
                "http://example.com",
                1000,
                2000,
                3000,
                4000
        );
    }

    @Test
    void testGetProductSimilarIds_Success() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), anyInt())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToFlux(Integer.class)).thenReturn(Flux.just(1, 2, 3));

        Flux<Integer> result = productOutService.getProductSimilarIds(1);

        StepVerifier.create(result)
                .expectNext(1, 2, 3)
                .verifyComplete();
    }

    @Test
    void testGetProductSimilarIds_NotFound() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), anyInt())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToFlux(Integer.class)).thenReturn(Flux.error(new ProductNotFoundException(1)));

        Flux<Integer> result = productOutService.getProductSimilarIds(1);

        StepVerifier.create(result)
                .expectError(ProductNotFoundException.class)
                .verify();
    }

    @Test
    void testGetProductDetail_Success() {
        Product product = Product.builder()
                .id("1")
                .name("Product 1")
                .build();

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), anyInt())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Product.class)).thenReturn(Mono.just(product));

        Mono<Product> result = productOutService.getProductDetail(1);

        StepVerifier.create(result)
                .expectNext(product)
                .verifyComplete();
    }

    @Test
    void testGetProductDetail_NotFound() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), anyInt())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Product.class)).thenReturn(Mono.error(new ProductNotFoundException(1)));

        Mono<Product> result = productOutService.getProductDetail(1);

        StepVerifier.create(result)
                .expectError(ProductNotFoundException.class)
                .verify();
    }

    @Test
    void testGetProductSimilarIdsFallback() {
        when(cacheManager.getCache(anyString())).thenReturn(cache);

        WebClientRequestException webClientRequestException = mock(WebClientRequestException.class);
        Flux<Integer> result = productOutService.getProductSimilarIdsFallback(1, webClientRequestException);

        StepVerifier.create(result)
                .verifyComplete();

        verify(cache).put(eq(1), any());
    }

    @Test
    void testGetProductDetailFallback() {
        when(cacheManager.getCache(anyString())).thenReturn(cache);

        WebClientRequestException webClientRequestException = mock(WebClientRequestException.class);
        Mono<Product> result = productOutService.getProductDetailFallback(1, webClientRequestException);

        StepVerifier.create(result)
                .verifyComplete();

        verify(cache).put(eq(1), any());
    }
}