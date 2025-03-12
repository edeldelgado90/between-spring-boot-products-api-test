package com.between.products.adapter.in.rest;

import com.between.products.application.dto.ProductDTO;
import com.between.products.application.mapper.ProductMapper;
import com.between.products.domain.product.Product;
import com.between.products.domain.product.ProductRequestException;
import com.between.products.port.out.rest.ProductOutPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import static org.mockito.Mockito.when;

@WebFluxTest(ProductController.class)
@Import(ProductControllerIntegrationTest.MockConfig.class)
class ProductControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ProductOutPort productOutPort;

    @Autowired
    private ProductMapper productMapper;

    @BeforeEach
    void resetMocks() {
        Mockito.reset(productOutPort);
        Mockito.reset(productMapper);
    }

    @Test
    void getSimilarProducts_shouldReturnProductDTOs() {
        // Arrange
        int productId = 1;
        Flux<Integer> similarIds = Flux.just(2, 3);
        ProductDTO productDTO2 = ProductDTO.builder()
                .id("2")
                .name("Product 2")
                .price(BigDecimal.valueOf(20.0))
                .availability(true)
                .build();
        ProductDTO productDTO3 = ProductDTO.builder()
                .id("3")
                .name("Product 3")
                .price(BigDecimal.valueOf(30.0))
                .availability(true)
                .build();

        Product expectedProduct2 = Product.builder()
                .id("2")
                .name("Product 2")
                .price(BigDecimal.valueOf(20.0))
                .availability(true)
                .build();

        Product expectedProduct3 = Product.builder()
                .id("3")
                .name("Product 3")
                .price(BigDecimal.valueOf(30.0))
                .availability(true)
                .build();

        when(productOutPort.getProductSimilarIds(productId)).thenReturn(similarIds);
        when(productOutPort.getProductDetail(2)).thenReturn(Mono.just(expectedProduct2));
        when(productOutPort.getProductDetail(3)).thenReturn(Mono.just(expectedProduct3));
        when(productMapper.toProductDTO(Mockito.any(Product.class))).thenAnswer(invocation -> {
            Product product = invocation.getArgument(0);
            return ProductDTO.builder()
                    .id(product.getId())
                    .name(product.getName())
                    .price(product.getPrice())
                    .availability(product.getAvailability())
                    .build();
        });

        // Act
        webTestClient.get()
                .uri("/product/{productId}/similar", productId)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ProductDTO.class)
                .contains(productDTO2, productDTO3);

        // Assert
        Mockito.verify(productOutPort).getProductSimilarIds(productId);
        Mockito.verify(productOutPort).getProductDetail(2);
        Mockito.verify(productOutPort).getProductDetail(3);
    }

    @Test
    void getSimilarProducts_shouldHandleErrorsGracefully() {
        // Arrange
        int productId = 1;
        Flux<Integer> similarIds = Flux.just(2, 3);

        when(productOutPort.getProductSimilarIds(productId)).thenReturn(similarIds);

        when(productOutPort.getProductDetail(2)).thenReturn(Mono.error(new ProductRequestException(2, HttpStatus.INTERNAL_SERVER_ERROR)));

        // Act
        webTestClient.get()
                .uri("/product/{productId}/similar", productId)
                .exchange()
                .expectStatus().is5xxServerError();

        // Assert
        Mockito.verify(productOutPort).getProductSimilarIds(productId);
        Mockito.verify(productOutPort).getProductDetail(2);
    }


    static class MockConfig {
        @Bean
        public ProductOutPort productOutPort() {
            return Mockito.mock(ProductOutPort.class);
        }

        @Bean
        public ProductMapper productMapper() {
            return Mockito.mock(ProductMapper.class);
        }
    }
}