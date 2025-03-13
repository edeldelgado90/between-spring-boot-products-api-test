package com.between.products.adapter.in.grpc;

import com.between.products.adapter.in.grpc.proto.GetProduct;
import com.between.products.adapter.in.grpc.proto.ProductResponse;
import com.between.products.adapter.in.grpc.proto.ProductServiceGrpc;
import com.between.products.domain.product.Product;
import com.between.products.port.out.rest.ProductOutPort;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import reactor.core.publisher.Flux;

@GrpcService
public class GRPCProductService extends ProductServiceGrpc.ProductServiceImplBase {
    private final ProductOutPort productOutPort;

    public GRPCProductService(ProductOutPort productOutPort) {
        this.productOutPort = productOutPort;
    }

    @Override
    public void getSimilarProducts(GetProduct request, StreamObserver<ProductResponse> responseObserver) {
        String productId = request.getProductId();
        Flux<Integer> productSimilarIds = productOutPort.getProductSimilarIds(Integer.valueOf(productId));
        Flux<com.between.products.adapter.in.grpc.proto.Product> products = productSimilarIds
                .flatMap(productOutPort::getProductDetail)
                .map(this::mapToGrpcProduct);

        products.collectList()
                .subscribe(productList -> {
                    ProductResponse response = ProductResponse.newBuilder()
                            .addAllProducts(productList)
                            .build();

                    responseObserver.onNext(response);
                    responseObserver.onCompleted();
                }, responseObserver::onError);
    }

    private com.between.products.adapter.in.grpc.proto.Product mapToGrpcProduct(Product domainProduct) {
        return com.between.products.adapter.in.grpc.proto.Product.newBuilder()
                .setId(domainProduct.getId())
                .setName(domainProduct.getName())
                .setPrice(domainProduct.getPrice().doubleValue())
                .setAvailability(domainProduct.getAvailability())
                .build();
    }
}
