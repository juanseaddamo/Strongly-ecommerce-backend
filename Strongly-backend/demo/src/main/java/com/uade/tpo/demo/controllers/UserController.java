package com.uade.tpo.demo.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.uade.tpo.demo.entity.User;
import com.uade.tpo.demo.entity.dto.UserRequest;
import com.uade.tpo.demo.service.UserService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/users") //localhost:4002/users
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // Crear usuario
    @PostMapping
    public ResponseEntity<UserResponse> create(@Valid @RequestBody UserRequest req) {
        User u = userService.createUser(req.getFullName(), req.getEmail(), req.getPassword());
        return ResponseEntity.ok(UserResponse.from(u));
    }

    // Obtener por id (solo activos)
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getById(@PathVariable Long id) {
        User u = userService.getUserById(id);
        return ResponseEntity.ok(UserResponse.from(u));
    }

    // Listar activos
    @GetMapping
    public ResponseEntity<List<UserResponse>> list() {
        List<UserResponse> list = userService.getAllUsers().stream()
                .map(UserResponse::from).toList();
        return ResponseEntity.ok(list);
    }

    // Soft delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @Data
    @AllArgsConstructor
    public static class UserResponse {
        private Long id;
        private String fullName;
        private String email;
        private String role;

        public static UserResponse from(User u) {
            return new UserResponse(
                u.getId(),
                u.getFullName(),
                u.getEmail(),
                u.getRole() != null ? u.getRole().name() : null
            );
        }
    }
}
