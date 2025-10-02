package com.uade.tpo.demo.entity.dto;

import java.math.BigDecimal;

public record CheckoutItemResponse(
        String name,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal subtotal
) {}