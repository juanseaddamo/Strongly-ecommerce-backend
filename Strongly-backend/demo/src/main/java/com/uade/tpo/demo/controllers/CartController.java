package com.uade.tpo.demo.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.uade.tpo.demo.entity.dto.*;
import com.uade.tpo.demo.entity.CartItem;
import com.uade.tpo.demo.service.CartService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    // Lista items + total
    @GetMapping
    public ResponseEntity<CartResponse> getCart(@RequestParam Long userId) {
        var cart = cartService.getOrCreateByUser(userId);
        var items = cartService.computeTotal(userId); // total
        List<CartItem> raw = cart.getItems(); // si tenés mappedBy; si no, fetch desde repo

        var list = (raw == null ? List.<CartItem>of() : raw).stream().map(ci ->
            new CartItemResponse(
                ci.getProduct().getId(),
                ci.getProduct().getName(),
                ci.getQuantity(),
                ci.getUnitPrice(),
                ci.getSubtotal()
            )
        ).toList();

        return ResponseEntity.ok(new CartResponse(list, cartService.computeTotal(userId)));
    }

    @PostMapping("/items")
    public ResponseEntity<CartItemResponse> addItem(@RequestParam Long userId,
                                                    @RequestBody AddItemRequest req) {
        CartItem ci = cartService.addItem(userId, req.productId(), req.quantity());
        return ResponseEntity.ok(new CartItemResponse(
            ci.getProduct().getId(),
            ci.getProduct().getName(),
            ci.getQuantity(),
            ci.getUnitPrice(),
            ci.getSubtotal()
        ));
    }

    @PutMapping("/items/{productId}")
    public ResponseEntity<?> setQty(@RequestParam Long userId,
                                    @PathVariable Long productId,
                                    @RequestBody SetQtyRequest req) {
        CartItem ci = cartService.setQuantity(userId, productId, req.quantity());
        if (ci == null) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(new CartItemResponse(
            ci.getProduct().getId(),
            ci.getProduct().getName(),
            ci.getQuantity(),
            ci.getUnitPrice(),
            ci.getSubtotal()
        ));
    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<Void> remove(@RequestParam Long userId,
                                       @PathVariable Long productId) {
        cartService.removeItem(userId, productId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> clear(@RequestParam Long userId) {
        cartService.clear(userId);
        return ResponseEntity.noContent().build();
    }
}
