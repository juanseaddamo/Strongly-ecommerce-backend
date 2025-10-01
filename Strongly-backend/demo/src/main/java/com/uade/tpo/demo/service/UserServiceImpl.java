package com.uade.tpo.demo.service;

import java.time.Instant;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uade.tpo.demo.entity.Cart;
import com.uade.tpo.demo.entity.User;
import com.uade.tpo.demo.entity.enums.Role;
import com.uade.tpo.demo.repository.CartRepository;
import com.uade.tpo.demo.repository.UserRepository;


import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    //private final PasswordEncoder passwordEncoder; // ver bean más abajo

    @Override
    @Transactional
    public User createUser(String fullName, String email, String rawPassword) {
        // Validación básica
        userRepository.findByEmailAndIsActiveTrue(email).ifPresent(u -> {
            throw new IllegalArgumentException("Email ya en uso");
        });

        User u = new User();
        u.setFullName(fullName);
        u.setEmail(email);
        u.setPasswordHash(rawPassword   ); // TODO hashear con passwordEncoder
        u.setRole(Role.BUYER);            // por defecto
        u.setIsActive(true);
        u.setCreatedAt(Instant.now());
        Cart existingCart = cartRepository.findByUserId(u.getId());
        if (existingCart == null) {
            Cart cart = new Cart();
            cart.setUser(u);   
            cartRepository.save(cart);
        }

        try {
            return userRepository.save(u);
        } catch (DataIntegrityViolationException ex) {
            // por si existe un usuario inactivo con el mismo email (único en DB)
            throw new IllegalArgumentException("Email ya existe en el sistema", ex);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado o inactivo"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAllActiveUsers();
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        User u = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        if (!Boolean.TRUE.equals(u.getIsActive())) return; // ya inactivo

        // Soft delete: se marca inactivo (nota: email sigue siendo único)
        u.setIsActive(false);
        userRepository.save(u);

        // Si quisieras liberar el email para re-registro:
        // u.setEmail(u.getEmail() + "__deleted__" + u.getId());
        // userRepository.save(u);
    }
}
