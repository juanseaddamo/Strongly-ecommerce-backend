package com.uade.tpo.demo.entity;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.uade.tpo.demo.entity.enums.Role;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user") // si tu tabla se llama "user"
public class User implements UserDetails{
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

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(this.role.name()));
    }

    @Override
    public String getPassword() {
        return this.passwordHash;
    }

    @Override
    public String getUsername() {
         return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
    // Si no tienes lógica de expiración de cuenta, devuelve true.
    return true;
    }

    @Override
    public boolean isAccountNonLocked() {
    // Si no tienes lógica de bloqueo de cuenta, devuelve true.
    return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
    // Si no tienes lógica de expiración de credenciales (contraseña), devuelve true.
    return true;
    }

    @Override
    public boolean isEnabled() {
    // Aquí puedes usar un campo de tu entidad, como 'isActive'.
    return this.isActive;
    }

}
