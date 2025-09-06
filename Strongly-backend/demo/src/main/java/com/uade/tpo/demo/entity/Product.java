package com.uade.tpo.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "products")
@Data
public class Product {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, length=150)
    private String name;

    @Column(nullable=false, length=180, unique=true)
    private String slug;

    @Column(columnDefinition="TEXT")
    private String description;

    @ManyToOne(optional=false)
    @JoinColumn(name="category_id")
    private Category category;

    @Column(nullable=false, precision=12, scale=2)
    private BigDecimal price;

    @Column(nullable=false)
    private Integer stock;

    @Column(name="is_active", nullable=false)
    private Boolean isActive = true;

    @Column(name="created_at", nullable=false)
    private Instant createdAt = Instant.now();

    @Column(name="updated_at")
    private Instant updatedAt;

    @ManyToOne
    @JoinColumn(name="created_by")
    private User createdBy;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductImage> images;

    // 👇 Evitar recursión: no serializar colecciones inversas
    @OneToMany(mappedBy = "product")
    @JsonIgnore
    private List<OrderItem> orderItems;

    @OneToMany(mappedBy = "product")
    @JsonIgnore
    private List<CartItem> cartItems;

    @PreUpdate
    public void touch() { this.updatedAt = Instant.now(); }
}
