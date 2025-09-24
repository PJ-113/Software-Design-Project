package com.ecom.api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderDto(Long id, Long userId, LocalDateTime createdAt, BigDecimal total, List<OrderItemDto> items) {}
