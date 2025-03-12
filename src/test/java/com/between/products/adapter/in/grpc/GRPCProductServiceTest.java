package com.between.products.adapter.in.grpc;

import com.between.products.adapter.in.grpc.proto.GetProduct;
import com.between.products.adapter.in.grpc.proto.ProductResponse;
import com.between.products.domain.product.Product;
import com.between.products.port.out.rest.ProductOutPort;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GRPCProductServiceTest {

    private GRPCProductService grpcProductService;
    private ProductOutPort productOutPort;
    private StreamObserver<ProductResponse> responseObserver;

    @BeforeEach
    void setUp() {
        productOutPort = mock(ProductOutPort.class);
        grpcProductService = new GRPCProductService(productOutPort);
        responseObserver = mock(StreamObserver.class);
    }

    @Test
    void getSimilarProducts_shouldReturnProductResponse_whenProductsExist() {
        // Arrange
        GetProduct request = GetProduct.newBuilder()
                .setProductId("123")
                .build();

        when(productOutPort.getProductSimilarIds(anyInt())).thenReturn(Flux.just(1, 2, 3));

        when(productOutPort.getProductDetail(1))
                .thenReturn(Mono.just(Product.builder().id("1").name("Product A").price(BigDecimal.valueOf(100.0)).availability(true).build()));
        when(productOutPort.getProductDetail(2))
                .thenReturn(Mono.just(Product.builder().id("2").name("Product B").price(BigDecimal.valueOf(200.0)).availability(true).build()));
        when(productOutPort.getProductDetail(3))
                .thenReturn(Mono.just(Product.builder().id("3").name("Product C").price(BigDecimal.valueOf(300.0)).availability(true).build()));

        // Act
        grpcProductService.getSimilarProducts(request, responseObserver);

        // Assert
        Mockito.verify(responseObserver).onNext(Mockito.argThat(response -> {
            List<com.between.products.adapter.in.grpc.proto.Product> products = response.getProductsList();
            return products.size() == 3 &&
                    products.get(0).getId().equals("1") &&
                    products.get(1).getId().equals("2") &&
                    products.get(2).getId().equals("3");
        }));
        Mockito.verify(responseObserver).onCompleted();
    }

    @Test
    void getSimilarProducts_shouldHandleError_whenProductOutPortFails() {
        // Arrange
        GetProduct request = GetProduct.newBuilder()
                .setProductId("123")
                .build();

        // Simular un error en productOutPort.getProductSimilarIds
        when(productOutPort.getProductSimilarIds(anyInt()))
                .thenReturn(Flux.error(new RuntimeException("Error fetching similar IDs")));

        // Act
        grpcProductService.getSimilarProducts(request, responseObserver);

        // Assert
        Mockito.verify(responseObserver).onError(Mockito.any(Throwable.class));
    }
}