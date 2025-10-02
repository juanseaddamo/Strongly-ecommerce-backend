package com.uade.tpo.demo.entity.dto;

import java.math.BigDecimal;


public record CartItemResponse(Long productId, String name, Integer quantity,
BigDecimal unitPrice, BigDecimal subtotal) {
    
}
