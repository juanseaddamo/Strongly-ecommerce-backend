package com.uade.tpo.demo.service;

import java.math.BigDecimal;

import com.uade.tpo.demo.entity.Cart;
import com.uade.tpo.demo.entity.CartItem;

public interface CartService {

    Cart getOrCreateByUser(Long userId);

    CartItem addItem(Long userId, Long productId, int qty);

    CartItem setQuantity(Long userId, Long productId, int qty);
    
    void removeItem(Long userId, Long productId);

    void clear(Long userId);

    BigDecimal computeTotal(Long userId);
}