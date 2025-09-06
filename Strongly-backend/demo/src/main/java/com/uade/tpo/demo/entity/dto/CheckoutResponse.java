package com.uade.tpo.demo.entity.dto;

import java.math.BigDecimal;

public record CheckoutResponse(Long orderId, BigDecimal total, String status) {}