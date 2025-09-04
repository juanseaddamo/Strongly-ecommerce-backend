package com.uade.tpo.demo.entity.dto;
import java.util.Locale.Product;

import com.uade.tpo.demo.entity.User;

import lombok.Data;
@Data
public class ProdutRequest{
    private long id;
    private String name;
    private String description;
    private User createdBy;
    private BigDecimal price;
    private int stock;
    private String description;

    private long parent_id;
}