package com.uade.tpo.demo.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uade.tpo.demo.entity.Cart;
import com.uade.tpo.demo.entity.CartItem;
import com.uade.tpo.demo.entity.Order;
import com.uade.tpo.demo.entity.OrderItem;
import com.uade.tpo.demo.repository.CartItemRepository;
import com.uade.tpo.demo.repository.CartRepository;
import com.uade.tpo.demo.repository.OrderRepository;
import com.uade.tpo.demo.repository.ProductRepository;
import com.uade.tpo.demo.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepo;
    private final CartItemRepository itemRepo;
    private final UserRepository userRepo;
    private final ProductRepository productRepo;
    private final OrderRepository orderRepo;

    @Override
    @Transactional
public Cart createCartForUser(Long userId) {
    Cart cart = cartRepo.findByUserId(userId);
    if (cart == null) {
        var user = userRepo.findById(userId)
            .orElseThrow(() -> new RuntimeException("User " + userId + " no existe"));
        cart = new Cart();
        cart.setUser(user);
        cart = cartRepo.save(cart);
    }
    return cart;
}

    @Override
    public Cart getCartByIdOrUser(Long cartId, Long userId) {
        if (cartId != null) {
            return cartRepo.findById(cartId)
                    .orElseThrow(() -> new RuntimeException("Cart " + cartId + " no existe"));
        }
        if (userId != null) {
            Cart cart = cartRepo.findByUserId(userId);
            if (cart == null) {
            throw new RuntimeException("User " + userId + " no tiene cart");
            }
        return cart;
        }
        throw new IllegalArgumentException("Falta cartId o userId");
    }

    @Override
    @Transactional
    public Cart addItem(Long cartId, Long productId, int qty) {
        if (qty <= 0) throw new IllegalArgumentException("quantity debe ser > 0");

        var cart = cartRepo.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart " + cartId + " no existe"));
        var product = productRepo.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product " + productId + " no existe"));

        var item = itemRepo.findByCartIdAndProductId(cartId, productId)
                .orElseGet(() -> {
                    var ci = new CartItem();
                    ci.setCart(cart);
                    ci.setProduct(product);
                    ci.setQuantity(0);
                    ci.setUnitPrice(product.getPrice()); // set inicial
                    ci.setSubtotal(BigDecimal.ZERO);      // set inicial
                    return ci;
                });

        int newQty = item.getQuantity() + qty;
        item.setQuantity(newQty);
        item.setUnitPrice(product.getPrice()); // precio actual del producto
        item.setSubtotal(product.getPrice().multiply(BigDecimal.valueOf(newQty)));

        itemRepo.save(item);

        // devolver el cart actualizado
        return getCartByIdOrUser(cartId, null);
    }

    @Override
    @Transactional
    public Cart updateItemQty(Long cartId, Long productId, int qty) {
        if (qty < 0) throw new IllegalArgumentException("quantity no puede ser negativo");
        var item = itemRepo.findByCartIdAndProductId(cartId, productId)
                .orElseThrow(() -> new RuntimeException("Item no existe en el cart"));

        if (qty == 0) {
            itemRepo.delete(item);
        } else {
            // recalcular con el precio actual del producto
            var product = item.getProduct();
            item.setQuantity(qty);
            item.setUnitPrice(product.getPrice());
            item.setSubtotal(product.getPrice().multiply(BigDecimal.valueOf(qty)));
            itemRepo.save(item);
        }

        return getCartByIdOrUser(cartId, null);
    }

    @Override
    @Transactional
    public void removeItem(Long cartId, Long productId) {
        itemRepo.deleteByCartIdAndProductId(cartId, productId);
    }

    @Override
    @Transactional
    public void clearCart(Long cartId) {
        var items = itemRepo.findByCartId(cartId);
        itemRepo.deleteAll(items);
    }

    @Override
    @Transactional
    public Order checkout(Long userId){
        Cart cart = cartRepo.findByUserId(userId);
        if (cart == null) {
            throw new RuntimeException("Carrito no encontrado.");
        }

        Order order = new Order();
        order.setUser(cart.getUser());
        order.setCreatedAt(Instant.now());

        List<OrderItem> orderItems = cart.getItems().stream().map(cartItem -> {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setSubtotal(cartItem.getSubtotal());
            orderItem.setUnitPrice(cartItem.getUnitPrice());
            return orderItem;
        }).collect(Collectors.toList());

        order.setItems(orderItems);

        BigDecimal totalPrice = orderItems.stream()
        .map(OrderItem::getSubtotal)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setTotal(totalPrice);

        Order savedOrder= orderRepo.save(order);

        clearCart(cart.getId());

        return savedOrder;
    }
}
