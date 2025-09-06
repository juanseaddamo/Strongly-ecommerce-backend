package com.uade.tpo.demo.service;

import java.util.List;

import com.uade.tpo.demo.entity.Order;

public interface OrderService {
    Order checkout(Long userId);
    Order get(Long orderId);
    List<Order> listByUser(Long userId);
}