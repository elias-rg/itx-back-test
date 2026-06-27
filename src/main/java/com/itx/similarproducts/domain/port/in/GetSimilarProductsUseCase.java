package com.itx.similarproducts.domain.port.in;

import com.itx.similarproducts.domain.model.ProductDetail;
import reactor.core.publisher.Mono;

import java.util.List;

public interface GetSimilarProductsUseCase {

    Mono<List<ProductDetail>> getSimilarProducts(String productId);
}
