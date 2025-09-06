package com.uade.tpo.demo.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uade.tpo.demo.entity.*;
import com.uade.tpo.demo.exceptions.*;
import com.uade.tpo.demo.service.CartService;
import com.uade.tpo.demo.repository.*;
import com.uade.tpo.demo.exceptions.*;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepo;
    private final CartItemRepository cartItemRepo;
    private final ProductRepository productRepo;

    @Override @Transactional(readOnly = true)
    public Cart getOrCreateByUser(Long userId) {
        return cartRepo.findByUserId(userId)
                .orElseGet(() -> {
                    Cart c = new Cart();
                    User u = new User(); u.setId(userId); // referencia “ligera”
                    c.setUser(u);
                    return cartRepo.save(c);
                });
    }

    @Override @Transactional
    public CartItem addItem(Long userId, Long productId, int qty) {
        if (qty <= 0) throw new BusinessException("quantity debe ser > 0");
        Cart cart = getOrCreateByUser(userId);
        Product p = productRepo.findById(productId)
                .orElseThrow(() -> new NotFoundException("Producto no encontrado"));

        if (Boolean.FALSE.equals(p.getIsActive()))
            throw new BusinessException("Producto inactivo: " + p.getName());

        CartItem item = cartItemRepo.findByCartIdAndProductId(cart.getId(), p.getId())
                .orElseGet(() -> {
                    CartItem ci = new CartItem();
                    ci.setCart(cart);
                    ci.setProduct(p);
                    ci.setQuantity(0);
                    ci.setUnitPrice(p.getPrice()); // “congelado”
                    ci.setSubtotal(BigDecimal.ZERO);
                    return ci;
                });

        item.setQuantity(item.getQuantity() + qty);
        item.setSubtotal(item.getUnitPrice()
                .multiply(BigDecimal.valueOf(item.getQuantity())));
        return cartItemRepo.save(item);
    }

    @Override @Transactional
    public CartItem setQuantity(Long userId, Long productId, int qty) {
        Cart cart = getOrCreateByUser(userId);
        CartItem item = cartItemRepo.findByCartIdAndProductId(cart.getId(), productId)
                .orElseThrow(() -> new NotFoundException("Item no existe en el carrito"));

        if (qty <= 0) { cartItemRepo.delete(item); return null; }

        item.setQuantity(qty);
        item.setSubtotal(item.getUnitPrice()
                .multiply(BigDecimal.valueOf(item.getQuantity())));
        return cartItemRepo.save(item);
    }

    @Override @Transactional
    public void removeItem(Long userId, Long productId) {
        Cart cart = getOrCreateByUser(userId);
        cartItemRepo.findByCartIdAndProductId(cart.getId(), productId)
            .ifPresent(cartItemRepo::delete);
    }

    @Override @Transactional
    public void clear(Long userId) {
        Cart cart = getOrCreateByUser(userId);
        List<CartItem> items = cartItemRepo.findByCartId(cart.getId());
        cartItemRepo.deleteAll(items);
    }

    @Override @Transactional(readOnly = true)
    public BigDecimal computeTotal(Long userId) {
        Cart cart = getOrCreateByUser(userId);
        return cartItemRepo.findByCartId(cart.getId()).stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
