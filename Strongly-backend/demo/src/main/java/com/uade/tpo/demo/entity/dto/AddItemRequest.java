package com.uade.tpo.demo.entity.dto;

import lombok.Data;

@Data
public class AddItemRequest {
    
    private Long productId;
    private Integer quantity;
}
