package com.itx.similarproducts.domain.model;

public record ProductDetail(
        String id,
        String name,
        Double price,
        Boolean availability
) {}
