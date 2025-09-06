package com.uade.tpo.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uade.tpo.demo.entity.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    

    Optional<CartItem> findByCartIdAndProductId(Long cartId, Long productId);


    List<CartItem> findByCartId(Long cartId);
}