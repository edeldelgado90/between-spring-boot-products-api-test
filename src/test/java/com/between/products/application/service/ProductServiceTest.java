package com.between.products.application.service;

import com.between.products.domain.product.Product;
import com.between.products.port.out.rest.ProductOutPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductOutPort productOutPort;

    @InjectMocks
    private ProductService productService;

    @Test
    void testGetSimilarProducts_Success() {
        Product product1 = Product.builder()
                .id("1")
                .name("Product 1")
                .build();

        Product product2 = Product.builder()
                .id("2")
                .name("Product 2")
                .build();

        when(productOutPort.getProductSimilarIds(anyInt())).thenReturn(Flux.just(1, 2));
        when(productOutPort.getProductDetail(1)).thenReturn(Mono.just(product1));
        when(productOutPort.getProductDetail(2)).thenReturn(Mono.just(product2));

        Flux<Product> result = productService.getSimilarProducts("123");

        StepVerifier.create(result)
                .expectNext(product1)
                .expectNext(product2)
                .verifyComplete();

        verify(productOutPort).getProductSimilarIds(123);
        verify(productOutPort).getProductDetail(1);
        verify(productOutPort).getProductDetail(2);
    }

    @Test
    void testGetSimilarProducts_NoSimilarIds() {
        when(productOutPort.getProductSimilarIds(anyInt())).thenReturn(Flux.empty());

        Flux<Product> result = productService.getSimilarProducts("123");

        StepVerifier.create(result)
                .verifyComplete();

        verify(productOutPort).getProductSimilarIds(123);
        verify(productOutPort, never()).getProductDetail(anyInt());
    }

    @Test
    void testGetSimilarProducts_ErrorFetchingDetails() {
        when(productOutPort.getProductSimilarIds(anyInt())).thenReturn(Flux.just(1, 2));
        when(productOutPort.getProductDetail(1)).thenReturn(Mono.error(new RuntimeException("Error fetching product")));
        when(productOutPort.getProductDetail(2)).thenReturn(Mono.just(Product.builder().build()));

        Flux<Product> result = productService.getSimilarProducts("123");

        StepVerifier.create(result)
                .expectNextCount(1)
                .verifyComplete();

        verify(productOutPort).getProductSimilarIds(123);
        verify(productOutPort).getProductDetail(1);
        verify(productOutPort).getProductDetail(2);
    }
}