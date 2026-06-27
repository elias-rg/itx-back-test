package com.itx.similarproducts.domain.port.out;

import com.itx.similarproducts.domain.model.ProductDetail;
import reactor.core.publisher.Mono;

public interface ProductDetailPort {

    Mono<ProductDetail> getProductDetail(String productId);
}
