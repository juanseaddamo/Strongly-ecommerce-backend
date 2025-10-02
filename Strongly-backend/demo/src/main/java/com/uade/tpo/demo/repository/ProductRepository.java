package com.uade.tpo.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.uade.tpo.demo.entity.Category;
import com.uade.tpo.demo.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    @Query(value = "select p from Product p where p.name = ?1")
    List<Product> findByname(String name);
        
    @Query("select p from Product p where p.category.id = ?1")
    List<Product> findByCategoryId(Long categoryId);
}
