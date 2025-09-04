package com.uade.tpo.demo.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.uade.tpo.demo.entity.Product;
import com.uade.tpo.demo.entity.dto.ProductRequest;
import com.uade.tpo.demo.exceptions.CategoryNotFoundException;
import com.uade.tpo.demo.exceptions.ProductDuplicateException;
import com.uade.tpo.demo.exceptions.ProductNotFoundException;

public interface ProductService {

    public Page<ProductRequest> getProduct(PageRequest pageRequest);

    public Optional<ProductRequest> getProductById(Long productId);

    public List<ProductRequest> getProductsByCategory(Long categoryId) throws CategoryNotFoundException; 

    public ProductRequest createProduct(String name,String description,int stock, BigDecimal price,long category_id, long id_user) throws ProductDuplicateException;

    public ProductRequest updatePrice(Long productId, BigDecimal newPrice) throws ProductNotFoundException;

    public ProductRequest updateStock(Long productId, int newStock) throws ProductNotFoundException;
}

