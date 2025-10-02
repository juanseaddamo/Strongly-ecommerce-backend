package com.uade.tpo.demo.controllers;

import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.uade.tpo.demo.entity.Order;
import com.uade.tpo.demo.entity.dto.CheckoutResponse;
import com.uade.tpo.demo.entity.dto.OrderSummaryResponse;
import com.uade.tpo.demo.service.OrderService;
import com.uade.tpo.demo.service.CartService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final CartService cartService;

    @PostMapping("/checkout")
    public ResponseEntity<CheckoutResponse> checkout(@PathVariable Long userId) {
    return ResponseEntity.ok(cartService.checkout(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> get(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.get(id));
    }

    @GetMapping("/mine")
public ResponseEntity<List<OrderSummaryResponse>> listMine(@RequestParam Long userId) {
    return ResponseEntity.ok(
        orderService.listByUser(userId).stream()
            .map(o -> new OrderSummaryResponse(
                o.getId(),
                o.getTotal(),
                o.getStatus().name()
            ))
            .toList()
    );
}
}
