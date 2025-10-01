package com.uade.tpo.demo.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uade.tpo.demo.entity.*;
import com.uade.tpo.demo.entity.enums.OrderStatus;
import com.uade.tpo.demo.exceptions.*;
import com.uade.tpo.demo.repository.*;


import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final CartRepository cartRepo;
    private final CartItemRepository cartItemRepo;
    private final ProductRepository productRepo;
    private final OrderRepository orderRepo;
    private final OrderItemRepository orderItemRepo;
    private final CartService cartService;

    @Override @Transactional
    public Order checkout(Long userId) {
        Cart cart = cartRepo.findByUserId(userId);
        if (cart == null) {
            throw new BusinessException("No existe el carrito");
        }

        List<CartItem> items = cartItemRepo.findByCartId(cart.getId());
        if (items.isEmpty()) throw new BusinessException("Carrito vacío");

        // Validaciones
        for (CartItem ci : items) {
            Product p = ci.getProduct();
            if (Boolean.FALSE.equals(p.getIsActive()))
                throw new BusinessException("Producto inactivo: " + p.getName());
            if (p.getStock() < ci.getQuantity())
                throw new BusinessException("Stock insuficiente: " + p.getName());
        }

        // Crear orden
        Order order = new Order();
        order.setUser(cart.getUser());
        order.setStatus(OrderStatus.CREATED);
        order.setTotal(BigDecimal.ZERO);
        order = orderRepo.save(order);

        BigDecimal total = BigDecimal.ZERO;

        // Copiar items + descontar stock
        for (CartItem ci : items) {
            OrderItem oi = new OrderItem();
            oi.setOrder(order);
            oi.setProduct(ci.getProduct());
            oi.setQuantity(ci.getQuantity());
            oi.setUnitPrice(ci.getUnitPrice());
            oi.setSubtotal(ci.getSubtotal());
            orderItemRepo.save(oi);

            Product p = ci.getProduct();
            p.setStock(p.getStock() - ci.getQuantity());
            productRepo.save(p);

            total = total.add(ci.getSubtotal());
        }

        order.setTotal(total);
        orderRepo.save(order);

        // Limpiar carrito
        cartItemRepo.deleteAll(items);

        // (Opcional) crear Shipment vacío aquí

        return order;
    }

    @Override @Transactional(readOnly = true)
    public Order get(Long orderId) {
        return orderRepo.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Orden no encontrada"));
    }

    @Override @Transactional(readOnly = true)
    public List<Order> listByUser(Long userId) {
        return orderRepo.findByUserIdOrderByCreatedAtDesc(userId);
    }
}
