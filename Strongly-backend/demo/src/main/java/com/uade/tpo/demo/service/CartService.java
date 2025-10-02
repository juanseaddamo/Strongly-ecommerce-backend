package com.uade.tpo.demo.service;

import com.uade.tpo.demo.entity.dto.CartItemResponse;
import com.uade.tpo.demo.entity.dto.CheckoutResponse;
import java.util.List;

public interface CartService {
    List<CartItemResponse> createCartForUser(Long userId);
    List<CartItemResponse> getCartByIdOrUser(Long cartId, Long userId);
    List<CartItemResponse> addItem(Long cartId, Long productId, int qty);
    List<CartItemResponse> updateItemQty(Long cartId, Long productId, int qty);
    void removeItem(Long cartId, Long productId);
    void clearCart(Long cartId);
    CheckoutResponse checkout(Long userId);
}