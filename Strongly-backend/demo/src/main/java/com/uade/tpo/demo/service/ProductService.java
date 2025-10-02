package com.uade.tpo.demo.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.uade.tpo.demo.entity.dto.ProductRequest;
import com.uade.tpo.demo.entity.dto.ProductResponse;
import com.uade.tpo.demo.exceptions.CategoryNotFoundException;
import com.uade.tpo.demo.exceptions.ProductDuplicateException;
import com.uade.tpo.demo.exceptions.ProductNotFoundException;

public interface ProductService {

    // Cambié PageRequest por Pageable y ProductRequest por ProductResponse
    Page<ProductResponse> getProduct(Pageable pageable);

    Optional<ProductResponse> getProductById(Long productId);

    List<ProductResponse> getProductsByCategory(Long categoryId) throws CategoryNotFoundException;

    ProductResponse createProduct(String name, String description, int stock, BigDecimal price, long category_id, long id_user)
            throws ProductDuplicateException, CategoryNotFoundException;

    ProductResponse updatePrice(Long productId, BigDecimal newPrice) throws ProductNotFoundException;

    ProductResponse updateStock(Long productId, int newStock) throws ProductNotFoundException;
}
