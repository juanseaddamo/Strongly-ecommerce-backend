package com.uade.tpo.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

//DATO PARA EL SERVICE
/*
no recalcules unitPrice desde product.getPrice() cada vez; 
sólo la primera vez que el producto entra al carrito.
Así, si el precio del catálogo cambia, el carrito y luego la orden conservan el valor original.
*/

@Entity
@Table(name = "cart_items", uniqueConstraints = @UniqueConstraint(name = "uk_cart_product", columnNames = {"cart_id","product_id"}))
@Data
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    // ======= nuevos campos para “congelar” el precio en el carrito =======
    @Column(name = "unit_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal unitPrice;  // copia de products.price al momento de agregar

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;   // unitPrice * quantity
}
