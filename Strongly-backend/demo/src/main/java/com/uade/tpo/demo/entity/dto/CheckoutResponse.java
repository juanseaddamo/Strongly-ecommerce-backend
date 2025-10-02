package com.uade.tpo.demo.entity.dto;

import java.math.BigDecimal;
import java.util.List;

public record CheckoutResponse(
        List<CheckoutItemResponse> items,
        BigDecimal total
) {}