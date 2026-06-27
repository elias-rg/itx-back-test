package com.itx.similarproducts.infrastructure.adapter.in.web;

import com.itx.similarproducts.domain.model.ProductDetail;
import com.itx.similarproducts.domain.model.exception.ProductNotFoundException;
import com.itx.similarproducts.domain.port.in.GetSimilarProductsUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.Mockito.when;

@WebFluxTest(SimilarProductsController.class)
class SimilarProductsControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private GetSimilarProductsUseCase getSimilarProductsUseCase;

    @Test
    void getSimilarProducts_returns200WithList() {
        List<ProductDetail> products = List.of(
                new ProductDetail("2", "Dress", 19.99, true),
                new ProductDetail("3", "Blazer", 29.99, false)
        );
        when(getSimilarProductsUseCase.getSimilarProducts("1")).thenReturn(Mono.just(products));

        webTestClient.get()
                .uri("/product/1/similar")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ProductDetail.class)
                .hasSize(2);
    }

    @Test
    void getSimilarProducts_returns404WhenProductNotFound() {
        when(getSimilarProductsUseCase.getSimilarProducts("999"))
                .thenReturn(Mono.error(new ProductNotFoundException("999")));

        webTestClient.get()
                .uri("/product/999/similar")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void getSimilarProducts_returns200WithEmptyList() {
        when(getSimilarProductsUseCase.getSimilarProducts("1")).thenReturn(Mono.just(List.of()));

        webTestClient.get()
                .uri("/product/1/similar")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ProductDetail.class)
                .hasSize(0);
    }
}
