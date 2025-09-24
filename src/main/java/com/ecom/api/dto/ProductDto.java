package com.ecom.api.dto;

import java.math.BigDecimal;

public record ProductDto(
        Long id,
        String name,
        String description,
        BigDecimal price,
        long stock,
        String imagePath
) {}
