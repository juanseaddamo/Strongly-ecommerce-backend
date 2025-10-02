package com.uade.tpo.demo.entity.dto;

import java.math.BigDecimal;

public record OrderSummaryResponse(
    Long id,
    BigDecimal total,
    String status
) {}