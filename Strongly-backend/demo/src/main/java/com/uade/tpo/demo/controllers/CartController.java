package com.uade.tpo.demo.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.uade.tpo.demo.entity.Cart;
import com.uade.tpo.demo.entity.Order;
import com.uade.tpo.demo.entity.dto.AddItemRequest;
import com.uade.tpo.demo.service.CartService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    // Crear carrito para un usuario
    // POST /cart?userId=3
    @PostMapping
    public ResponseEntity<Cart> createCart(@RequestParam Long userId) {
        var cart = cartService.createCartForUser(userId);
        return ResponseEntity.ok(cart);
    }

    // Obtener carrito por cartId o por userId
    // GET /cart?cartId=1  o  GET /cart?userId=3
    @GetMapping
    public ResponseEntity<Cart> getCart(@RequestParam(required=false) Long cartId,
                                        @RequestParam(required=false) Long userId) {
        var cart = cartService.getCartByIdOrUser(cartId, userId);
        return ResponseEntity.ok(cart);
    }

    // Agregar item
    // POST /cart/items  body: { "cartId":1, "productId":1, "quantity":2 }
    @PostMapping("/items")
    public ResponseEntity<Cart> addItem(@RequestBody AddItemRequest req) {
        if (req.getCartId() == null || req.getProductId() == null || req.getQuantity() == null) {
            return ResponseEntity.badRequest().build();
        }
        var cart = cartService.addItem(req.getCartId(), req.getProductId(), req.getQuantity());
        return ResponseEntity.ok(cart);
    }

    // Actualizar cantidad
    // PATCH /cart/items  body: { "cartId":1, "productId":1, "quantity":5 }
    @PatchMapping("/items")
    public ResponseEntity<Cart> updateItemQty(@RequestBody AddItemRequest req) {
        if (req.getCartId() == null || req.getProductId() == null || req.getQuantity() == null) {
            return ResponseEntity.badRequest().build();
        }
        var cart = cartService.updateItemQty(req.getCartId(), req.getProductId(), req.getQuantity());
        return ResponseEntity.ok(cart);
    }

    // Eliminar item
    // DELETE /cart/items?cartId=1&productId=1
    @DeleteMapping("/items")
    public ResponseEntity<Void> removeItem(@RequestParam Long cartId, @RequestParam Long productId) {
        cartService.removeItem(cartId, productId);
        return ResponseEntity.noContent().build();
    }

    // Vaciar carrito
    // DELETE /cart/clear?cartId=1
    @DeleteMapping("/clear")
    public ResponseEntity<Void> clear(@RequestParam Long cartId) {
        cartService.clearCart(cartId);
        return ResponseEntity.noContent().build();
    }

    // Confirma el carrito y lo convierte en orden
    @PostMapping("/{userId}/checkout")
    public ResponseEntity<Order> checkout(@PathVariable Long userId) {
       
        Order order = cartService.checkout(userId);
        return ResponseEntity.ok(order);
        
    }
   
   
}
