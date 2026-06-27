package com.itx.similarproducts.domain.port.out;

import reactor.core.publisher.Mono;

import java.util.List;

public interface SimilarProductIdsPort {

    Mono<List<String>> getSimilarProductIds(String productId);
}
