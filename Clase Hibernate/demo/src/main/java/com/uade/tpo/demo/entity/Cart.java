package com.uade.tpo.demo.entity;

import java.time.Instant;
import java.util.List;
import jakarta.persistence.*;
import jakarta.persistence.Index;
import lombok.Data;

@Entity
@Table(name = "carts",
       indexes = @Index(name = "idx_cart_user_unique", columnList = "user_id", unique = true))
@Data
public class Cart {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    @Column(name = "created_at")
    private Instant createdAt;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items;

    @PrePersist public void prePersist() { if (createdAt == null) createdAt = Instant.now(); }
}
