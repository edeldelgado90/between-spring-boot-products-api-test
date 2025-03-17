package com.between.products.adapter.in.rest;

import com.between.products.adapter.mapper.ProductMapper;
import com.between.products.application.dto.ProductDTO;
import com.between.products.domain.product.Product;
import com.between.products.port.in.ProductInPort;
import com.between.products.port.out.rest.ProductOutPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import java.math.BigDecimal;

import static org.mockito.Mockito.when;

@WebFluxTest(ProductController.class)
@Import(ProductControllerIntegrationTest.MockConfig.class)
class ProductControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ProductInPort productInPort;

    @Autowired
    private ProductMapper productMapper;

    @BeforeEach
    void resetMocks() {
        Mockito.reset(productInPort);
        Mockito.reset(productMapper);
    }

    @Test
    void getSimilarProducts_shouldReturnProductDTOs() {
        // Arrange
        String productId = "1";
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

        when(productInPort.getSimilarProducts(productId)).thenReturn(Flux.just(expectedProduct2, expectedProduct3));
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
        Mockito.verify(productInPort).getSimilarProducts(productId);
    }

    @Test
    void getSimilarProducts_shouldHandleErrorsGracefully() {
        // Arrange
        String productId = "1";

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

        when(productInPort.getSimilarProducts(productId)).thenReturn(Flux.just(expectedProduct2, expectedProduct3));


        // Act
        webTestClient.get()
                .uri("/product/{productId}/similar", productId)
                .exchange()
                .expectStatus().is5xxServerError();

        // Assert
        Mockito.verify(productInPort).getSimilarProducts(productId);
    }


    static class MockConfig {
        @Bean
        public ProductInPort productInPort() {
            return Mockito.mock(ProductInPort.class);
        }

        @Bean
        public ProductMapper productMapper() {
            return Mockito.mock(ProductMapper.class);
        }
    }
}