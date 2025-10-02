package com.uade.tpo.demo.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.security.Principal;

import com.uade.tpo.demo.entity.User;
import com.uade.tpo.demo.entity.dto.AddItemRequest;
import com.uade.tpo.demo.entity.dto.CartResponse;
import com.uade.tpo.demo.entity.dto.CartItemResponse;
import com.uade.tpo.demo.entity.dto.CheckoutResponse;
import com.uade.tpo.demo.repository.UserRepository;
import com.uade.tpo.demo.service.CartService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final UserRepository userRepository;

    // Obtener carrito del usuario (con ID del carrito)
    @GetMapping
    public ResponseEntity<CartResponse> getMyCart(Principal principal) {
        String email = principal.getName();
        User user = userRepository.findByEmailAndIsActiveTrue(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        CartResponse cart = cartService.getCartWithItems(user.getId());
        return ResponseEntity.ok(cart);
    }

    

    // Agregar item al carrito
    @PostMapping("/items")
    public ResponseEntity<CartResponse> addItem(@RequestBody AddItemRequest req, Principal principal) {
        if (req.getProductId() == null || req.getQuantity() == null) {
            return ResponseEntity.badRequest().build();
        }

        String email = principal.getName();
        User user = userRepository.findByEmailAndIsActiveTrue(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        CartResponse updatedCart = cartService.addItem(user.getId(), req.getProductId(), req.getQuantity());
        return ResponseEntity.ok(updatedCart);
    }

    // Actualizar cantidad de un item
    @PatchMapping("/items")
    public ResponseEntity<CartResponse> updateItemQty(@RequestBody AddItemRequest req, Principal principal) {
        if (req.getProductId() == null || req.getQuantity() == null) {
            return ResponseEntity.badRequest().build();
        }

        String email = principal.getName();
        User user = userRepository.findByEmailAndIsActiveTrue(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        CartResponse updatedCart = cartService.updateItemQty(user.getId(), req.getProductId(), req.getQuantity());
        return ResponseEntity.ok(updatedCart);
    }

    // Eliminar un item del carrito
    @DeleteMapping("/items")
    public ResponseEntity<Void> removeItem(@RequestParam Long productId, Principal principal) {
        String email = principal.getName();
        User user = userRepository.findByEmailAndIsActiveTrue(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        cartService.removeItem(user.getId(), productId);
        return ResponseEntity.noContent().build();
    }

    // Vaciar carrito
    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearCart(Principal principal) {
        String email = principal.getName();
        User user = userRepository.findByEmailAndIsActiveTrue(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        cartService.clearCart(user.getId());
        return ResponseEntity.noContent().build();
    }

    // Checkout
    @PostMapping("/checkout")
    public ResponseEntity<CheckoutResponse> checkout(Principal principal) {
        String email = principal.getName();
        User user = userRepository.findByEmailAndIsActiveTrue(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        CheckoutResponse response = cartService.checkout(user.getId());
        return ResponseEntity.ok(response);
    }
}