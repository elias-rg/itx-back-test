package com.itx.similarproducts.infrastructure.adapter.in.web;

import com.itx.similarproducts.domain.model.ProductDetail;
import com.itx.similarproducts.domain.model.exception.ProductNotFoundException;
import com.itx.similarproducts.domain.port.in.GetSimilarProductsUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
public class SimilarProductsController {

    private final GetSimilarProductsUseCase getSimilarProductsUseCase;

    public SimilarProductsController(GetSimilarProductsUseCase getSimilarProductsUseCase) {
        this.getSimilarProductsUseCase = getSimilarProductsUseCase;
    }

    @GetMapping("/product/{productId}/similar")
    public Mono<ResponseEntity<List<ProductDetail>>> getSimilarProducts(@PathVariable String productId) {
        return getSimilarProductsUseCase.getSimilarProducts(productId)
                .map(ResponseEntity::ok);
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<Void> handleProductNotFound() {
        return ResponseEntity.notFound().build();
    }
}
