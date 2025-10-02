package com.uade.tpo.demo.service;

import com.uade.tpo.demo.entity.dto.CartItemResponse;
import com.uade.tpo.demo.entity.dto.CartResponse;
import com.uade.tpo.demo.entity.dto.CheckoutResponse;
import java.util.List;

public interface CartService {
    CartResponse getCartWithItems(Long userId); // reemplaza createCartForUser
    CartResponse addItem(Long userId, Long productId, int qty);
    CartResponse updateItemQty(Long userId, Long productId, int qty);
    void removeItem(Long userId, Long productId);
    void clearCart(Long userId);
    CheckoutResponse checkout(Long userId);

}