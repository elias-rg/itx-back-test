package com.itx.similarproducts.application.service;

import com.itx.similarproducts.domain.model.ProductDetail;
import com.itx.similarproducts.domain.port.in.GetSimilarProductsUseCase;
import com.itx.similarproducts.domain.port.out.ProductDetailPort;
import com.itx.similarproducts.domain.port.out.SimilarProductIdsPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class SimilarProductsService implements GetSimilarProductsUseCase {

    private static final Logger log = LoggerFactory.getLogger(SimilarProductsService.class);

    private final SimilarProductIdsPort similarProductIdsPort;
    private final ProductDetailPort productDetailPort;

    public SimilarProductsService(SimilarProductIdsPort similarProductIdsPort,
                                  ProductDetailPort productDetailPort) {
        this.similarProductIdsPort = similarProductIdsPort;
        this.productDetailPort = productDetailPort;
    }

    @Override
    public Mono<List<ProductDetail>> getSimilarProducts(String productId) {
        return similarProductIdsPort.getSimilarProductIds(productId)
                .flatMapMany(Flux::fromIterable)
                .flatMap(id -> productDetailPort.getProductDetail(id)
                        .onErrorResume(e -> {
                            log.warn("Skipping similar product {}: {}", id, e.getMessage());
                            return Mono.empty();
                        }))
                .collectList();
    }
}
