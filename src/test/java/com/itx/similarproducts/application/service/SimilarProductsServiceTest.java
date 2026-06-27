package com.itx.similarproducts.application.service;

import com.itx.similarproducts.domain.model.ProductDetail;
import com.itx.similarproducts.domain.model.exception.ProductNotFoundException;
import com.itx.similarproducts.domain.port.out.ProductDetailPort;
import com.itx.similarproducts.domain.port.out.SimilarProductIdsPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SimilarProductsServiceTest {

    @Mock
    private SimilarProductIdsPort similarProductIdsPort;

    @Mock
    private ProductDetailPort productDetailPort;

    private SimilarProductsService service;

    @BeforeEach
    void setUp() {
        service = new SimilarProductsService(similarProductIdsPort, productDetailPort);
    }

    @Test
    void getSimilarProducts_returnsList() {
        ProductDetail product1 = new ProductDetail("2", "Dress", 19.99, true);
        ProductDetail product2 = new ProductDetail("3", "Blazer", 29.99, false);

        when(similarProductIdsPort.getSimilarProductIds("1")).thenReturn(Mono.just(List.of("2", "3")));
        when(productDetailPort.getProductDetail("2")).thenReturn(Mono.just(product1));
        when(productDetailPort.getProductDetail("3")).thenReturn(Mono.just(product2));

        StepVerifier.create(service.getSimilarProducts("1"))
                .expectNextMatches(list -> list.size() == 2
                        && list.contains(product1)
                        && list.contains(product2))
                .verifyComplete();
    }

    @Test
    void getSimilarProducts_skipsUnavailableProductDetails() {
        ProductDetail product1 = new ProductDetail("2", "Dress", 19.99, true);

        when(similarProductIdsPort.getSimilarProductIds("1")).thenReturn(Mono.just(List.of("2", "99")));
        when(productDetailPort.getProductDetail("2")).thenReturn(Mono.just(product1));
        when(productDetailPort.getProductDetail("99")).thenReturn(Mono.error(new ProductNotFoundException("99")));

        StepVerifier.create(service.getSimilarProducts("1"))
                .expectNextMatches(list -> list.size() == 1 && list.contains(product1))
                .verifyComplete();
    }

    @Test
    void getSimilarProducts_propagatesErrorWhenProductNotFound() {
        when(similarProductIdsPort.getSimilarProductIds("999"))
                .thenReturn(Mono.error(new ProductNotFoundException("999")));

        StepVerifier.create(service.getSimilarProducts("999"))
                .expectError(ProductNotFoundException.class)
                .verify();
    }

    @Test
    void getSimilarProducts_returnsEmptyListWhenNoSimilarIds() {
        when(similarProductIdsPort.getSimilarProductIds("1")).thenReturn(Mono.just(List.of()));

        StepVerifier.create(service.getSimilarProducts("1"))
                .expectNextMatches(List::isEmpty)
                .verifyComplete();
    }
}
