package com.ecom.api.dto;

public record CartItemDto(Long id, Long productId, String productName, int quantity) {}
