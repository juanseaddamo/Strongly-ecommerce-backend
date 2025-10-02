package com.uade.tpo.demo.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.uade.tpo.demo.entity.Cart;
import com.uade.tpo.demo.entity.Order;
import com.uade.tpo.demo.entity.dto.AddItemRequest;
import com.uade.tpo.demo.entity.dto.CartItemResponse;
import com.uade.tpo.demo.entity.dto.CheckoutResponse;
import com.uade.tpo.demo.service.CartService;
import lombok.RequiredArgsConstructor;



@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor


public class CartController {

    private final CartService cartService;

   
    @GetMapping
    public ResponseEntity<List<CartItemResponse>> getCartItems(
        @RequestParam(required = false) Long cartId,
        @RequestParam(required = false) Long userId) {

    List<CartItemResponse> items = cartService.getCartByIdOrUser(cartId, userId);
    return ResponseEntity.ok(items);
}

    @PostMapping("/items")
    public ResponseEntity<List<CartItemResponse>> addItem(@RequestBody AddItemRequest req) {
        if (req.getCartId() == null || req.getProductId() == null || req.getQuantity() == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(cartService.addItem(req.getCartId(), req.getProductId(), req.getQuantity()));
    }

    @PatchMapping("/items")
    public ResponseEntity<List<CartItemResponse>> updateItemQty(@RequestBody AddItemRequest req) {
        if (req.getCartId() == null || req.getProductId() == null || req.getQuantity() == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(cartService.updateItemQty(req.getCartId(), req.getProductId(), req.getQuantity()));
    }

    @DeleteMapping("/items")
    public ResponseEntity<Void> removeItem(@RequestParam Long cartId, @RequestParam Long productId) {
        cartService.removeItem(cartId, productId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Void> clear(@RequestParam Long cartId) {
        cartService.clearCart(cartId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{userId}/checkout")
    public ResponseEntity<CheckoutResponse> checkout(@PathVariable Long userId) {
        return ResponseEntity.ok(cartService.checkout(userId));
    }
}