package com.between.products.adapter.out.rest;

import com.between.products.domain.product.Product;
import com.between.products.domain.product.ProductNotFoundException;
import com.between.products.domain.product.ProductRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.function.Function;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ProductServiceTest {

    private ProductOutService productService;
    private ResponseSpec responseSpec;

    @BeforeEach
    void setUp() {
        WebClient.Builder webClientBuilder = mock(WebClient.Builder.class);
        WebClient webClient = mock(WebClient.class);
        RequestHeadersUriSpec requestHeadersUriSpec = mock(RequestHeadersUriSpec.class);
        RequestHeadersSpec requestHeadersSpec = mock(RequestHeadersSpec.class);
        responseSpec = mock(ResponseSpec.class);

        when(webClientBuilder.baseUrl(anyString())).thenReturn(webClientBuilder);
        when(webClientBuilder.clientConnector(any())).thenReturn(webClientBuilder);
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), any(Object.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

        productService = new ProductOutService(webClientBuilder,
                "http://mock-api.com",
                2000,
                2000,
                2000,
                2000);
    }

    @Test
    void getProductSimilarIds_shouldReturnSimilarIds_whenApiReturnsSuccess() {
        // Arrange
        Integer[] similarIds = {2, 3, 4};
        given(responseSpec.onStatus(any(), any())).willReturn(responseSpec);
        given(responseSpec.bodyToFlux(Integer.class)).willReturn(Flux.just(similarIds));

        // Act
        Flux<Integer> result = productService.getProductSimilarIds(1);

        // Assert
        StepVerifier.create(result)
                .expectNext(2, 3, 4)
                .verifyComplete();
    }

    @Test
    void getProductSimilarIds_shouldThrowProductRequestException_whenApiReturnsError() {
        // Arrange
        given(responseSpec.onStatus(any(), any()))
                .willAnswer(invocation -> {
                    Function<ClientResponse, Mono<? extends Throwable>> errorHandler = invocation.getArgument(1);

                    ClientResponse clientResponse = mock(ClientResponse.class);
                    when(clientResponse.statusCode()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);

                    errorHandler.apply(clientResponse).subscribe();

                    return responseSpec;
                });

        given(responseSpec.bodyToFlux(Integer.class))
                .willReturn(Flux.error(new ProductRequestException(1, HttpStatus.INTERNAL_SERVER_ERROR)));

        // Act
        Flux<Integer> result = productService.getProductSimilarIds(1);

        // Assert
        StepVerifier.create(result)
                .expectError(ProductRequestException.class)
                .verify();
    }

    @Test
    void getProductDetail_shouldReturnProduct_whenApiReturnsSuccess() {
        // Arrange
        Product product = Product.builder()
                .id("1")
                .name("Product 1")
                .price(BigDecimal.valueOf(100))
                .availability(true)
                .build();
        given(responseSpec.onStatus(any(), any())).willReturn(responseSpec);
        given(responseSpec.bodyToMono(Product.class)).willReturn(Mono.just(product));

        // Act
        Mono<Product> result = productService.getProductDetail(1);

        // Assert
        StepVerifier.create(result)
                .expectNext(product)
                .verifyComplete();
    }

    @Test
    void getProductDetail_shouldThrowProductNotFoundException_whenApiReturns4xx() {
        // Arrange
        given(responseSpec.onStatus(any(), any()))
                .willAnswer(invocation -> {
                    Function<ClientResponse, Mono<? extends Throwable>> errorHandler = invocation.getArgument(1);

                    ClientResponse clientResponse = mock(ClientResponse.class);
                    when(clientResponse.statusCode()).thenReturn(HttpStatus.NOT_FOUND);

                    errorHandler.apply(clientResponse).subscribe();

                    return responseSpec;
                });
        given(responseSpec.bodyToMono(Product.class)).willReturn(Mono.error(new ProductNotFoundException(1)));

        // Act
        Mono<Product> result = productService.getProductDetail(1);

        // Assert
        StepVerifier.create(result)
                .expectError(ProductNotFoundException.class)
                .verify();
    }

    @Test
    void getProductDetail_shouldThrowProductRequestException_whenApiReturns5xx() {
        // Arrange
        given(responseSpec.onStatus(any(), any()))
                .willAnswer(invocation -> {
                    Function<ClientResponse, Mono<? extends Throwable>> errorHandler = invocation.getArgument(1);

                    ClientResponse clientResponse = mock(ClientResponse.class);
                    when(clientResponse.statusCode()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);

                    errorHandler.apply(clientResponse).subscribe();

                    return responseSpec;
                });
        given(responseSpec.bodyToMono(Product.class)).willReturn(Mono.error(new ProductRequestException(1, HttpStatus.INTERNAL_SERVER_ERROR)));

        // Act
        Mono<Product> result = productService.getProductDetail(1);

        // Assert
        StepVerifier.create(result)
                .expectError(ProductRequestException.class)
                .verify();
    }
}