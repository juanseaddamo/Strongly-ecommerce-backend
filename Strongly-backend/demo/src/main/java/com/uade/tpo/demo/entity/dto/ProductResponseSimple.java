package com.uade.tpo.demo.entity.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponseSimple {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private String createdByEmail;
}