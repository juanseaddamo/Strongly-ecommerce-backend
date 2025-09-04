package com.uade.tpo.demo.service;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.uade.tpo.demo.entity.Product;
import com.uade.tpo.demo.exceptions.ProductDuplicateException;
import com.uade.tpo.demo.exceptions.ProductNotFoundException;

public interface ProductService {

    public Page<Product> getProduct(PageRequest pageRequest);

    public Optional<Product> getProductById(Long productId);

    public Page<Product> getProductsByCategory(Long categoryId);

    public Product createProduct(String name,String description,int stock, BigDecimal price,int category_id) throws ProductDuplicateException;

    public Product updatePrice(Long productId, BigDecimal newPrice) throws ProductNotFoundException;

    public Product updateStock(Long productId, Integer newStock) throws ProductNotFoundException;
}