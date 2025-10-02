package com.uade.tpo.demo.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import com.uade.tpo.demo.entity.Cart;
import com.uade.tpo.demo.entity.CartItem;
import com.uade.tpo.demo.entity.Order;
import com.uade.tpo.demo.entity.OrderItem;
import com.uade.tpo.demo.entity.User;
import com.uade.tpo.demo.entity.dto.CartItemResponse;
import com.uade.tpo.demo.entity.dto.CartResponse;
import com.uade.tpo.demo.entity.dto.CheckoutItemResponse;
import com.uade.tpo.demo.entity.dto.CheckoutResponse;
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

    // ----------------- Helpers -----------------

    private Cart getCartByUser(Long userId) {
        Cart cart = cartRepo.findByUserId(userId);
        if (cart == null) {
            User user = userRepo.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            cart = new Cart();
            cart.setUser(user);
            cart = cartRepo.save(cart);
        }
        return cart;
    }

    private List<CartItemResponse> mapToDtoList(Cart cart) {
        return cart.getItems().stream()
                .map(item -> new CartItemResponse(
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getSubtotal()
                ))
                .toList();
    }

    // ----------------- Servicio -----------------

@Override
@Transactional
public CartResponse getCartWithItems(Long userId) {
    Cart cart = getCartByUser(userId); // obtiene o crea el carrito del usuario
    List<CartItemResponse> itemsDto = mapToDtoList(cart);

    // calcular el total sumando todos los subtotales
    BigDecimal total = itemsDto.stream()
            .map(CartItemResponse::subtotal) // accede al subtotal de cada item
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    // devolver CartResponse con id, items y total
    return new CartResponse(cart.getId(), itemsDto, total);
}

    @Override
@Transactional
public CartResponse addItem(Long userId, Long productId, int qty) {
    if (qty <= 0) throw new IllegalArgumentException("La cantidad debe ser mayor a 0");

    Cart cart = getCartByUser(userId);

    // Validar que el producto exista
    var product = productRepo.findById(productId)
            .orElseThrow(() -> new RuntimeException("El producto con id " + productId + " no existe"));

    CartItem item = itemRepo.findByCartIdAndProductId(cart.getId(), productId)
            .orElseGet(() -> {
                CartItem ci = new CartItem();
                ci.setCart(cart);
                ci.setProduct(product);
                ci.setQuantity(0);
                ci.setUnitPrice(product.getPrice());
                ci.setSubtotal(BigDecimal.ZERO);
                return ci;
            });

    int newQty = item.getQuantity() + qty;
    item.setQuantity(newQty);
    item.setUnitPrice(product.getPrice());
    item.setSubtotal(product.getPrice().multiply(BigDecimal.valueOf(newQty)));

    itemRepo.save(item);

    // --- ACÁ CALCULAMOS itemsDto y total ---
    List<CartItemResponse> itemsDto = mapToDtoList(cart);
    BigDecimal total = itemsDto.stream()
            .map(CartItemResponse::subtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    return new CartResponse(cart.getId(), itemsDto, total);
}

 @Override
@Transactional
public CartResponse updateItemQty(Long userId, Long productId, int qty) {
    Cart cart = getCartByUser(userId);

    CartItem item = itemRepo.findByCartIdAndProductId(cart.getId(), productId)
            .orElseThrow(() -> new RuntimeException("El item no existe en el carrito"));

    if (qty == 0) {
        itemRepo.delete(item);
    } else {
        item.setQuantity(qty);
        item.setUnitPrice(item.getProduct().getPrice());
        item.setSubtotal(item.getProduct().getPrice().multiply(BigDecimal.valueOf(qty)));
        itemRepo.save(item);
    }

    // --- CALCULAMOS itemsDto y total ---
    List<CartItemResponse> itemsDto = mapToDtoList(cart);
    BigDecimal total = itemsDto.stream()
            .map(CartItemResponse::subtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    return new CartResponse(cart.getId(), itemsDto, total);
}


    @Override
    @Transactional
        public void removeItem(Long userId, Long productId) {
        Cart cart = getCartByUser(userId);
        itemRepo.deleteByCartIdAndProductId(cart.getId(), productId);
    }

    @Override
    @Transactional
    public void clearCart(Long userId) {
        Cart cart = getCartByUser(userId);
        itemRepo.deleteByCartId(cart.getId());
    }

    @Override
    @Transactional
    public CheckoutResponse checkout(Long userId) {
        Cart cart = getCartByUser(userId);

        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("El carrito está vacío");
        }

        Order order = new Order();
        order.setUser(cart.getUser());
        order.setCreatedAt(Instant.now());

        List<OrderItem> orderItems = cart.getItems().stream().map(cartItem -> {
            OrderItem oi = new OrderItem();
            oi.setOrder(order);
            oi.setProduct(cartItem.getProduct());
            oi.setQuantity(cartItem.getQuantity());
            oi.setUnitPrice(cartItem.getUnitPrice());
            oi.setSubtotal(cartItem.getSubtotal());
            return oi;
        }).toList();

        order.setItems(orderItems);

        BigDecimal totalPrice = orderItems.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setTotal(totalPrice);
        orderRepo.save(order);

        List<CheckoutItemResponse> itemsDto = cart.getItems().stream()
                .map(i -> new CheckoutItemResponse(
                        i.getProduct().getName(),
                        i.getQuantity(),
                        i.getUnitPrice(),
                        i.getSubtotal()
                ))
                .toList();

        clearCart(userId);

        return new CheckoutResponse(itemsDto, totalPrice);
    }
}