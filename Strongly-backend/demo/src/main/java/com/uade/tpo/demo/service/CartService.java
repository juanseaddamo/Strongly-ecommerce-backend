package com.uade.tpo.demo.service;

import com.uade.tpo.demo.entity.Cart;

public interface CartService {
    Cart createCartForUser(Long userId);
    Cart getCartByIdOrUser(Long cartId, Long userId);
    Cart addItem(Long cartId, Long productId, int qty);
    Cart updateItemQty(Long cartId, Long productId, int qty);
    void removeItem(Long cartId, Long productId);
    void clearCart(Long cartId);
}
