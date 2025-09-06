package com.uade.tpo.demo.entity;

import java.time.Instant;
import java.util.List;

import com.uade.tpo.demo.entity.enums.Role;

import jakarta.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Data
@Table(name = "user") // si tu tabla se llama "user"
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, unique=true, length=120)
    private String email;

    @Column(name="password_hash", nullable=false, length=255)
    private String passwordHash;

    @Column(name="full_name", length=120)
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=10)
    private Role role = Role.BUYER;

    @Column(name="created_at")
    private Instant createdAt;

    @Column(name="is_active")
    private Boolean isActive = true;

    // Evitamos que al serializar User viaje toda la lista de órdenes
    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Order> orders;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = Instant.now();
    }
}
