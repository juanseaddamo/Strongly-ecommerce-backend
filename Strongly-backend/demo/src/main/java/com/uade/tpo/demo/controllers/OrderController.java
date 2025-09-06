package com.uade.tpo.demo.controllers;

import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.uade.tpo.demo.entity.Order;
import com.uade.tpo.demo.entity.dto.CheckoutResponse;
import com.uade.tpo.demo.service.OrderService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/checkout")
    public ResponseEntity<CheckoutResponse> checkout(@RequestParam Long userId) {
        Order o = orderService.checkout(userId);
        return ResponseEntity.ok(new CheckoutResponse(o.getId(), o.getTotal(), o.getStatus().name()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> get(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.get(id));
    }

    @GetMapping("/mine")
    public ResponseEntity<?> listMine(@RequestParam Long userId) {
        return ResponseEntity.ok(
            orderService.listByUser(userId).stream()
                .map(o -> new CheckoutResponse(o.getId(), o.getTotal(), o.getStatus().name()))
                .collect(Collectors.toList())
        );
    }
}
