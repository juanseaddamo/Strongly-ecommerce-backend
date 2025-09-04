package com.uade.tpo.demo.entity.dto;

import java.math.BigDecimal;

import com.uade.tpo.demo.entity.Product;
import com.uade.tpo.demo.entity.User;

import lombok.Data;
@Data        
public class ProductRequest{
    private long id;
    private String name;
    private String description;
    private Long id_User;
    private BigDecimal price;
    private int stock;
    private Long id_category;
    
    public ProductRequest() {
}

    public ProductRequest(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.description = product.getDescription();
        this.price = product.getPrice();
        this.stock = product.getStock();

        this.id_category = (product.getCategory() != null) ? product.getCategory().getId() : null;
        this.id_User = (product.getCreatedBy() != null) ? product.getCreatedBy().getId() : null;

}}


