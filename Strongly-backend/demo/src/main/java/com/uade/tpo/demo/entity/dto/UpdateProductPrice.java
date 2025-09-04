package com.uade.tpo.demo.entity.dto;

import java.math.BigDecimal;

import lombok.Data;
@Data
public class UpdateProductPrice {
  private long idProducto;
  private BigDecimal precio;  
}
