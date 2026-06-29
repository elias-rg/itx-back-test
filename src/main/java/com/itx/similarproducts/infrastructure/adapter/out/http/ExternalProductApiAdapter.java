package com.itx.similarproducts.infrastructure.adapter.out.http;

import com.itx.similarproducts.domain.model.ProductDetail;
import com.itx.similarproducts.domain.model.exception.ProductNotFoundException;
import com.itx.similarproducts.domain.port.out.ProductDetailPort;
import com.itx.similarproducts.domain.port.out.SimilarProductIdsPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

@Component
public class ExternalProductApiAdapter implements SimilarProductIdsPort, ProductDetailPort {

    private final WebClient webClient;
    private final Duration productDetailTimeout;

    public ExternalProductApiAdapter(WebClient externalApiWebClient,
                                     @Value("${external.api.product-detail-timeout-ms:2000}") long timeoutMs) {
        this.webClient = externalApiWebClient;
        this.productDetailTimeout = Duration.ofMillis(timeoutMs);
    }

    @Override
    public Mono<List<String>> getSimilarProductIds(String productId) {
        return webClient.get()
                .uri("/product/{productId}/similarids", productId)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        response -> Mono.error(new ProductNotFoundException(productId)))
                .bodyToMono(new ParameterizedTypeReference<List<String>>() {})
                .timeout(productDetailTimeout);
    }

    @Override
    public Mono<ProductDetail> getProductDetail(String productId) {
        return webClient.get()
                .uri("/product/{productId}", productId)
                .retrieve()
                .onStatus(HttpStatusCode::isError,
                        response -> Mono.error(new ProductNotFoundException(productId)))
                .bodyToMono(ProductDetail.class)
                .timeout(productDetailTimeout);
    }
}
